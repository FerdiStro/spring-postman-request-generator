package com.github.ferdistro.springpostmanrequestgenerator.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.ferdistro.springpostmanrequestgenerator.Info
import com.github.ferdistro.springpostmanrequestgenerator.Item
import com.github.ferdistro.springpostmanrequestgenerator.PostmanCollection
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import javax.swing.SwingConstants

@Service(Service.Level.PROJECT)
class PermanentCache(private val project: Project) {


    private val mapper = jacksonObjectMapper().registerKotlinModule()
    private val file = File("${project.basePath}/generated-request.json")


    private fun getDefaultInfo(): Info {
        return Info(
            name = "Generated Requests", schema = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
        )
    }

    fun addRequest(item: Item) {
        if (!file.exists()) {
            file.createNewFile()
        }
        var postmanCollection: PostmanCollection
        try {
            postmanCollection = mapper.readValue(file, PostmanCollection::class.java)
        } catch (ignore: Exception) {
            logger<PermanentCache>().warn(ignore.message)
            postmanCollection = PostmanCollection(null, null)
        }

        if (postmanCollection.info == null) {
            postmanCollection.info = getDefaultInfo()
        }

        if (postmanCollection.item == null) {
            postmanCollection.item = listOf(item)
        } else {
            postmanCollection.item = postmanCollection.item!! + listOf(item)
        }

        val jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(postmanCollection)

        file.writeText(jsonString)

        val virtualFile: VirtualFile? = VfsUtil.findFileByIoFile(file, true)
        virtualFile?.refresh(false, false)

        setFocusToGeneratedLine(jsonString, item)
    }

    fun setFocusToGeneratedLine(jsonString: String, item: Item) {
        val virtualFile = VfsUtil.findFileByIoFile(file, true) ?: return
        val editorManager = FileEditorManager.getInstance(project)

        val isAlreadyOpen = editorManager.openFiles.contains(virtualFile)

        if (!isAlreadyOpen) {
            val currentWindow = (editorManager as? FileEditorManagerEx)?.currentWindow
            currentWindow?.split(SwingConstants.VERTICAL, false, virtualFile, true)
        } else {
            editorManager.openFile(virtualFile, true)
        }

        val lines = jsonString.split("\n").toTypedArray()
        var lineNumber = 0
        for (i in lines.indices.reversed()) {
            if (lines[i].contains(item.name) && lines[i].contains("\"name\" : \"")) {
                lineNumber = i
                break
            }
        }
        val editor = editorManager.selectedTextEditor ?: return
        val document = editor.document
        val offset = document.getLineStartOffset(lineNumber)

        editor.caretModel.moveToOffset(offset)
        editor.scrollingModel.scrollToCaret(ScrollType.CENTER)
    }
}