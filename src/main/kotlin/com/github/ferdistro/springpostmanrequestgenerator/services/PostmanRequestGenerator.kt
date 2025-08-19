package com.github.ferdistro.springpostmanrequestgenerator.services

import com.github.ferdistro.springpostmanrequestgenerator.annotation.AnnotationData
import com.github.ferdistro.springpostmanrequestgenerator.annotation.AnnotationExtractor
import com.github.ferdistro.springpostmanrequestgenerator.annotation.RequestMappingAnnotationExtractor
import com.github.ferdistro.springpostmanrequestgenerator.postman.Item
import com.github.ferdistro.springpostmanrequestgenerator.postman.QueryItem
import com.github.ferdistro.springpostmanrequestgenerator.postman.Request
import com.github.ferdistro.springpostmanrequestgenerator.postman.URL
import com.intellij.openapi.components.Service
import com.intellij.psi.PsiMethod

@Service(Service.Level.PROJECT)
class PostmanRequestGenerator {
    private val baseUrl: String = "{{PROTOCOL}}{{SERVER}}"
    private val appContext: String = "{{APP_CONTEXT}}"

    private val extractors: List<AnnotationExtractor> = listOf(
        RequestMappingAnnotationExtractor(),
    )

    private val supportedAnnotations = extractors
        .map { it.annotationQualifiedName }
        .toSet()

    fun hasSupportedAnnotation(method: PsiMethod): Boolean {
        return supportedAnnotations.any { method.hasAnnotation(it) }
    }

    fun generateJson(method: PsiMethod) {
        val queryItems = extractQueryParameters(method)
        val annotationData = extractAnnotationData(method)
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

        val service = method.project.getService(PermanentCache::class.java)
        service.addRequest(item)
    }


    private fun extractQueryParameters(method: PsiMethod): List<QueryItem> {
        return method.parameterList.parameters.map { param ->
            QueryItem(key = param.name, value = "")
        }
    }

    private fun extractAnnotationData(method: PsiMethod): AnnotationData {
        return extractors
            .firstNotNullOfOrNull { it.extractAnnotationData(method) }
            ?: throw IllegalStateException("No supported annotation found on method: ${method.name}")
    }

    private fun buildUrlComponents(path: String, queryItems: List<QueryItem>): URL {
        val sanitizedPath = path.replace("\"", "")
        val rawUrl = buildRawUrl(sanitizedPath)
        val pathComponents = buildPathComponents(sanitizedPath)

        return URL(
            raw = rawUrl,
            host = listOf(baseUrl),
            path = pathComponents,
            query = queryItems
        )
    }


    private fun buildRawUrl(path: String): String {
        return if (path.isNotEmpty()) {
            "$baseUrl/$appContext$path"
        } else {
            "$baseUrl/$appContext"
        }
    }

    private fun buildPathComponents(path: String): List<String> {
        return if (path.isNotEmpty()) {
            listOf(appContext) + path.split("/").filter { it.isNotEmpty() }
        } else {
            listOf(appContext)
        }
    }
}