package com.github.ferdistro.springpostmanrequestgenerator

import com.github.ferdistro.springpostmanrequestgenerator.services.PermanentCache
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiNameValuePair
import com.intellij.psi.PsiTypeElement
import java.awt.event.MouseEvent
import javax.swing.Icon

class JsonGeneratorLineMarkerProvider : LineMarkerProvider {
    private val icon: Icon = IconLoader.getIcon("/META-INF/icon.svg", JsonGeneratorLineMarkerProvider::class.java)
    private val baseUrl: String = "{{PROTOCOL}}{{SERVER}}"
    private val appContext: String = "{{APP_CONTEXT}}"

    private val extractors: List<AnnotationExtractor> = listOf(
        RequestMappingAnnotationExtractor(),
    )

    private val supportedAnnotations = extractors
        .map { it.annotationQualifiedName }
        .toSet()

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is PsiMethod) return null

        if (!hasSupportedAnnotation(element)) return null

        val anchor = findAnchorElement(element)

        return LineMarkerInfo(
            /* element = */ anchor,
            /* range = */ anchor.textRange,
            /* icon = */ icon,
            /* tooltipProvider = */ { "Generate JSON" },
            /* navHandler = */ { _: MouseEvent?, _: Any? -> generateJson(element) },
            /* alignment = */ GutterIconRenderer.Alignment.RIGHT,
            /* accessibleNameProvider = */ { "Generate JSON" }
        )
    }

    private fun hasSupportedAnnotation(method: PsiMethod): Boolean {
        return supportedAnnotations.any { method.hasAnnotation(it) }
    }

    private fun findAnchorElement(method: PsiMethod): PsiElement {
        val anchor = method.modifierList.nextSibling ?: method
        return anchor as? PsiTypeElement ?: (method.nameIdentifier ?: method)
    }

    private fun generateJson(method: PsiMethod) {
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

data class AnnotationData(
    val name: String,
    val method: String,
    val path: String
)

abstract class AnnotationExtractor(
    val annotationQualifiedName: String
) {
    abstract fun extractAnnotationData(method: PsiMethod): AnnotationData?


    protected fun sanitizeValue(value: String): String {
        return value.replace("\"", "")
    }

    protected fun extractNameFromPath(path: String): String {
        return path.split("/").lastOrNull()?.takeIf { it.isNotEmpty() } ?: ""
    }

    protected fun extractHttpMethod(methodValue: String): String {
        return methodValue.split(".").getOrNull(1)?.replace("\"", "") ?: ""
    }

    protected fun findAnnotation(method: PsiMethod): PsiAnnotation? {
        return method.modifierList.findAnnotation(annotationQualifiedName)
    }

    protected fun extractAttributes(annotation: PsiAnnotation): Map<String, String> {
        return annotation.parameterList.attributes.associate { attr ->
            val attrName = attr.name ?: VALUE_ATTRIBUTE
            val attrValue = extractAttributeValue(attr)
            attrName to attrValue
        }
    }

    protected fun extractAttributeValue(attribute: PsiNameValuePair): String {
        return attribute.value?.let { value ->
            when {
                attribute.name == METHOD_ATTRIBUTE -> extractHttpMethod(value.text)
                else -> sanitizeValue(value.text)
            }
        }.orEmpty()
    }

    companion object{
        protected const val VALUE_ATTRIBUTE = "value"
        protected const val METHOD_ATTRIBUTE = "method"
    }
}

class RequestMappingAnnotationExtractor : AnnotationExtractor(
    annotationQualifiedName = "org.springframework.web.bind.annotation.RequestMapping"
) {

    companion object {
        private const val DEFAULT_HTTP_METHOD = "GET"
    }

    override fun extractAnnotationData(method: PsiMethod): AnnotationData? {
        val annotation = findAnnotation(method) ?: return null

        val attributes = extractAttributes(annotation)
        val path = attributes[AnnotationExtractor.VALUE_ATTRIBUTE].orEmpty()
        val httpMethod = attributes[AnnotationExtractor.METHOD_ATTRIBUTE].orEmpty()

        return AnnotationData(
            name = extractNameFromPath(path).ifEmpty { method.name },
            method = httpMethod.ifEmpty { DEFAULT_HTTP_METHOD },
            path = path
        )
    }

}