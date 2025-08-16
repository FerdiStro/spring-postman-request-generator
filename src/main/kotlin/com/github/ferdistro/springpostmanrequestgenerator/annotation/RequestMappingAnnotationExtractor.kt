package com.github.ferdistro.springpostmanrequestgenerator.annotation

import com.github.ferdistro.springpostmanrequestgenerator.MethodInfo

class RequestMappingAnnotationExtractor : AnnotationExtractor(
    annotationQualifiedName = "org.springframework.web.bind.annotation.RequestMapping"
) {

    companion object {
        private const val DEFAULT_HTTP_METHOD = "GET"
    }

    override fun extractAnnotationData(method: MethodInfo): AnnotationData? {
        val annotation = findAnnotation(method) ?: return null

        val attributes = annotation.attributes
        val path = attributes[VALUE_ATTRIBUTE].orEmpty()
        val httpMethod = attributes[METHOD_ATTRIBUTE].orEmpty()
        val methodName = method.name

        return AnnotationData(
            name = extractNameFromPath(path).ifEmpty { methodName },
            method = httpMethod.ifEmpty { DEFAULT_HTTP_METHOD },
            path = path
        )
    }

}