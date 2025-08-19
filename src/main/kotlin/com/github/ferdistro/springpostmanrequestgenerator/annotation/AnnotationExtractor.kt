package com.github.ferdistro.springpostmanrequestgenerator.annotation

import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiNameValuePair

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