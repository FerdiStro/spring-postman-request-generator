package com.github.ferdistro.springpostmanrequestgenerator

import com.github.ferdistro.springpostmanrequestgenerator.services.PermanentCache
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiTypeElement
import java.awt.event.MouseEvent
import javax.swing.Icon

private const val ANNOTATION_NAME = "org.springframework.web.bind.annotation.RequestMapping"

class JsonGeneratorLineMarkerProvider : LineMarkerProvider {
    private val icon: Icon = IconLoader.getIcon("/META-INF/icon.svg", JsonGeneratorLineMarkerProvider::class.java)
    private val baseUrl: String = "{{PROTOCOL}}{{SERVER}}"
    private val appContext: String = "{{APP_CONTEXT}}"

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element is PsiMethod && element.hasAnnotation(ANNOTATION_NAME)) {
            val anchor = element.modifierList.nextSibling ?: element
            val finalAnchor = anchor as? PsiTypeElement ?: (element.nameIdentifier ?: element)

            return LineMarkerInfo(
                /* element = */ finalAnchor,
                /* range = */ finalAnchor.textRange,
                /* icon = */ icon,
                /* tooltipProvider = */ { "Generate JSON" },
                /* navHandler = */ { _: MouseEvent?, _ -> generateJson(element) },
                /* alignment = */ GutterIconRenderer.Alignment.RIGHT,
                /* accessibleNameProvider = */ { "Generate JSON" })
        }
        return null
    }

    private fun generateJson(method: PsiMethod) {
        val stringBuilder = StringBuilder()

        val queryItems = mutableListOf<QueryItem>()
        for (param in method.parameterList.parameters) {
            stringBuilder.append("Parameter: ${param.name}; Type: ${param.type.presentableText}")
            queryItems.add(QueryItem(key = param.name, value = ""))

            val annotations = param.modifierList?.annotations ?: emptyArray()
            for (ann in annotations) {
                val annQualifiedName = ann.qualifiedName ?: "Unknown"
                stringBuilder.append("  Annotation: $annQualifiedName")

                val attributes = ann.parameterList.attributes
                for (attr in attributes) {
                    val name = attr.name ?: "value"
                    val value = attr.value?.text ?: "null"
                    stringBuilder.append("    Attribute: $name = $value")
                }
            }
        }

        val annotationData = extractAnnotationData(method, stringBuilder)

        val urlComponents = buildUrlComponents(annotationData.path, queryItems)

        val item = Item(
            name = annotationData.name,
            request = Request(
                method = annotationData.method,
                header = emptyList(),
                url = urlComponents
            ),
            response = emptyList()
        )

        val project = method.project
        val service = project.getService(PermanentCache::class.java)
        service.addRequest(item)
    }

    private data class AnnotationData(
        val name: String,
        val method: String,
        val path: String
    ) {
        companion object {
            val EMPTY = AnnotationData("", "", "")
        }
    }

    private fun extractAnnotationData(
        method: PsiMethod,
        stringBuilder: StringBuilder
    ): AnnotationData {
        val annotation = method.modifierList.findAnnotation(ANNOTATION_NAME)
            ?: return AnnotationData.EMPTY

        var name = ""
        var method = ""
        var path = ""

        val attributes = annotation.parameterList.attributes
        for (attr in attributes) {
            val attrName = attr.name ?: "value"
            val attrValue = attr.value?.text ?: "null"

            when (attrName) {
                "value" -> {
                    name = attrValue.split("/").last().replace("\"", "")
                    path = attrValue.replace("\"", "")
                }

                "method" -> {
                    method = attrValue.split(".").getOrElse(1) { "" }.replace("\"", "")
                }
            }

            stringBuilder.append("\"$attrName\" : \"$attrValue\",")
        }

        return AnnotationData(name, method, path)
    }

    private fun buildUrlComponents(
        path: String,
        queryItems: List<QueryItem>
    ): URL {
        val rawUrl = if (path.isNotEmpty()) {
            ("$baseUrl/$appContext$path").replace("\"", "")
        } else {
            ("$baseUrl/$appContext").replace("\"", "")
        }

        val pathComponents = if (path.isNotEmpty()) {
            listOf(appContext) + path.replace("\"", "").split("/").drop(1)
        } else {
            listOf(appContext)
        }

        return URL(
            raw = rawUrl,
            host = listOf(baseUrl),
            path = pathComponents,
            query = queryItems
        )
    }
}