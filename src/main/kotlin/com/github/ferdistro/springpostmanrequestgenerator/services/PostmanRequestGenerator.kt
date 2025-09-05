package com.github.ferdistro.springpostmanrequestgenerator.services

import com.github.ferdistro.springpostmanrequestgenerator.MethodInfo
import com.github.ferdistro.springpostmanrequestgenerator.annotation.AnnotationData
import com.github.ferdistro.springpostmanrequestgenerator.annotation.AnnotationExtractor
import com.github.ferdistro.springpostmanrequestgenerator.annotation.RequestMappingAnnotationExtractor
import com.github.ferdistro.springpostmanrequestgenerator.postman.Item
import com.github.ferdistro.springpostmanrequestgenerator.postman.QueryItem
import com.github.ferdistro.springpostmanrequestgenerator.postman.Request
import com.github.ferdistro.springpostmanrequestgenerator.postman.URL
import com.github.ferdistro.springpostmanrequestgenerator.settings.RequestGeneratorSettings
import com.intellij.openapi.components.Service

@Service(Service.Level.PROJECT)
class PostmanRequestGenerator {
    private fun baseUrl(): String {
        val settings = RequestGeneratorSettings.getInstance()
        val urlBuilder: StringBuilder = StringBuilder()
        if (settings.state.protocol.useEnv) {
            urlBuilder.append("{{PROTOCOL}}")
        }
        if (!settings.state.protocol.useEnv) {
            urlBuilder.append(settings.state.protocol.value)
        }

        if (settings.state.serverUrl.useEnv) {
            urlBuilder.append("{{SERVER}}")
        }
        if (!settings.state.serverUrl.useEnv) {
            urlBuilder.append(settings.state.serverUrl.value)
        }
        return urlBuilder.toString()
    }

    private fun appContext(): String {
        val settings = RequestGeneratorSettings.getInstance()
        if (settings.state.context.extra) return ""
        if (settings.state.context.useEnv) {
            return "/{{APP_CONTEXT}}"
        }
        return settings.state.context.value
    }


    private val extractors: List<AnnotationExtractor> = listOf(
        RequestMappingAnnotationExtractor(),
    )

    private val supportedAnnotations = extractors.map { it.annotationQualifiedName }.toSet()

    fun hasSupportedAnnotation(method: MethodInfo): Boolean {
        return supportedAnnotations.any { a ->
            method.annotations.any { ann -> ann.qualifiedName == a }
        }
    }

    fun generateJson(method: MethodInfo) {
        val queryItems = extractQueryParameters(method)
        val annotationData = extractAnnotationData(method)
        val urlComponents = buildUrlComponents(annotationData.path, queryItems)

        val item = Item(
            name = annotationData.name, request = Request(
                method = annotationData.method, header = emptyList(), url = urlComponents
            ), response = emptyList()
        )

        val service = method.project.getService(PermanentCache::class.java)
        service.addRequest(item)

        if (RequestGeneratorSettings.getInstance().state.apiActive) {
            val success = ConnectToPostmanApi(service).postCollection();
            println()
        }

    }


    private fun extractQueryParameters(method: MethodInfo): List<QueryItem> {
        val parameters = method.parameters

        return parameters.map { param ->
            QueryItem(key = param.name, value = "")
        }
    }

    private fun extractAnnotationData(method: MethodInfo): AnnotationData {
        return extractors.firstNotNullOfOrNull { it.extractAnnotationData(method) }
            ?: throw IllegalStateException("No supported annotation found on method: $method")
        //?: throw IllegalStateException("No supported annotation found on method: ${method.name}")
    }

    private fun buildUrlComponents(path: String, queryItems: List<QueryItem>): URL {


        val sanitizedPath = path.replace("\"", "")
        val rawUrl = buildRawUrl(sanitizedPath)
        val pathComponents = buildPathComponents(sanitizedPath)

        return URL(
            raw = rawUrl, host = listOf(baseUrl()), path = pathComponents, query = queryItems
        )
    }


    private fun buildRawUrl(path: String): String {
        return if (path.isNotEmpty()) {
            "${baseUrl()}${appContext()}$path"
        } else {
            "${baseUrl()}${appContext()}"
        }
    }

    private fun buildPathComponents(path: String): List<String> {
        return if (path.isNotEmpty()) {
            listOf(appContext()) + path.split("/").filter { it.isNotEmpty() }
        } else {
            listOf(appContext())
        }
    }
}