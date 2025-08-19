package com.github.ferdistro.springpostmanrequestgenerator.annotation

import com.intellij.psi.PsiMethod

class RequestMappingAnnotationExtractor : AnnotationExtractor(
    annotationQualifiedName = "org.springframework.web.bind.annotation.RequestMapping"
) {

    companion object {
        private const val DEFAULT_HTTP_METHOD = "GET"
    }

    override fun extractAnnotationData(method: PsiMethod): AnnotationData? {
        val annotation = findAnnotation(method) ?: return null

        val attributes = extractAttributes(annotation)
        val path = attributes[VALUE_ATTRIBUTE].orEmpty()
        val httpMethod = attributes[METHOD_ATTRIBUTE].orEmpty()

        return AnnotationData(
            name = extractNameFromPath(path).ifEmpty { method.name },
            method = httpMethod.ifEmpty { DEFAULT_HTTP_METHOD },
            path = path
        )
    }

}