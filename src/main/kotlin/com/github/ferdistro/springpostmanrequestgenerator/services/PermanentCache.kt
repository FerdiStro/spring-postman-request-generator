package com.github.ferdistro.springpostmanrequestgenerator.services

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.ferdistro.springpostmanrequestgenerator.postman.Info
import com.github.ferdistro.springpostmanrequestgenerator.postman.Item
import com.github.ferdistro.springpostmanrequestgenerator.postman.PostmanCollection
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import javax.swing.SwingConstants

@Service(Service.Level.PROJECT)
class PermanentCache(private val project: Project) {

    companion object {
        private val LOG = logger<PermanentCache>()
        private const val COLLECTION_FILE_NAME = "generated-request.json"
        private const val POSTMAN_SCHEMA = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
        private const val DEFAULT_COLLECTION_NAME = "Generated Requests"
    }

    private val mapper = jacksonObjectMapper().registerKotlinModule()
    private val collectionPath: Path = Paths.get(project.basePath ?: ".", COLLECTION_FILE_NAME)

    private fun createDefaultCollection(): PostmanCollection {
        return PostmanCollection(
            info = Info(
                name = DEFAULT_COLLECTION_NAME,
                schema = POSTMAN_SCHEMA
            ),
            item = emptyList()
        )
    }

    fun addRequest(item: Item) {
        try {
            val collection = loadOrCreateCollection()
            val updatedCollection = collection.copy(
                item = (collection.item ?: emptyList()) + item
            )

            saveCollection(updatedCollection)
            navigateToNewItem(updatedCollection, item)

        } catch (e: Exception) {
            LOG.error("Failed to add request item: ${item.name}", e)
        }
    }

    private fun loadOrCreateCollection(): PostmanCollection {
        return if (Files.exists(collectionPath)) {
            loadExistingCollection()
        } else {
            createDefaultCollection()
        }
    }

    private fun loadExistingCollection(): PostmanCollection {
        return try {
            val content = Files.readString(collectionPath)
            val collection = mapper.readValue<PostmanCollection>(content)

            collection.copy(
                info = collection.info ?: Info(DEFAULT_COLLECTION_NAME, POSTMAN_SCHEMA),
                item = collection.item ?: emptyList()
            )
        } catch (e: JsonProcessingException) {
            LOG.warn("Invalid JSON in collection file, creating new collection", e)
            createDefaultCollection()
        } catch (e: IOException) {
            LOG.warn("Could not read collection file, creating new collection", e)
            createDefaultCollection()
        }
    }

    private fun saveCollection(collection: PostmanCollection) {
        try {
            Files.createDirectories(collectionPath.parent)

            val jsonContent = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(collection)

            Files.writeString(
                collectionPath,
                jsonContent,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING
            )

            refreshVirtualFile()

        } catch (e: IOException) {
            LOG.error("Failed to save collection to file", e)
        } catch (e: JsonProcessingException) {
            LOG.error("Failed to serialize collection to JSON", e)
        }
    }

    private fun refreshVirtualFile() {
        val virtualFile = VfsUtil.findFileByIoFile(collectionPath.toFile(), true)
        virtualFile?.refresh(false, false)
    }

    private fun navigateToNewItem(collection: PostmanCollection, newItem: Item) {
        ApplicationManager.getApplication().invokeLater {
            try {
                val virtualFile = getVirtualFile() ?: return@invokeLater
                openFileInEditor(virtualFile)
                scrollToItem(virtualFile, newItem)
            } catch (e: Exception) {
                LOG.warn("Failed to navigate to new item: ${newItem.name}", e)
            }
        }
    }

    private fun getVirtualFile(): VirtualFile? {
        return VfsUtil.findFileByIoFile(collectionPath.toFile(), true)
    }

    private fun openFileInEditor(virtualFile: VirtualFile) {
        val editorManager = FileEditorManager.getInstance(project)
        val isAlreadyOpen = editorManager.openFiles.contains(virtualFile)

        if (!isAlreadyOpen) {
            val fileEditorManagerEx = editorManager as? FileEditorManagerEx
            val currentWindow = fileEditorManagerEx?.currentWindow

            if (currentWindow != null) {
                currentWindow.split(SwingConstants.VERTICAL, false, virtualFile, true)
            } else {
                editorManager.openFile(virtualFile, true)
            }
        } else {
            editorManager.openFile(virtualFile, true)
        }
    }

    private fun scrollToItem(virtualFile: VirtualFile, item: Item) {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return

        try {
            val document = editor.document
            val content = document.text
            val lineNumber = findItemLineNumber(content, item)

            if (lineNumber >= 0 && lineNumber < document.lineCount) {
                val offset = document.getLineStartOffset(lineNumber)
                editor.caretModel.moveToOffset(offset)
                editor.scrollingModel.scrollToCaret(ScrollType.CENTER)
            }
        } catch (e: Exception) {
            LOG.warn("Failed to scroll to item: ${item.name}", e)
        }
    }

    private fun findItemLineNumber(content: String, item: Item): Int {
        val lines = content.lines()
        val targetPattern = "\"name\" : \"${item.name}\","

        for (i in lines.indices.reversed()) {
            if (lines[i].contains(targetPattern)) {
                return i
            }
        }
        return -1
    }

    private fun escapeForRegex(input: String): String {
        return input.replace(Regex("[\\\\^$.*+?()\\[\\]{}|]")) { "\\${it.value}" }
    }
}
