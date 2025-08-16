package com.github.ferdistro.springpostmanrequestgenerator.line

import com.github.ferdistro.springpostmanrequestgenerator.AnnotationInfo
import com.github.ferdistro.springpostmanrequestgenerator.MethodInfo
import com.github.ferdistro.springpostmanrequestgenerator.ParameterInfo
import com.github.ferdistro.springpostmanrequestgenerator.services.PostmanRequestGenerator
import com.github.ferdistro.springpostmanrequestgenerator.util.IconHolder
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtNamedFunction

class KotlinLineMarkerProvider(
    private val postmanRequestGenerator: PostmanRequestGenerator,
) : PostmanLineMarkerProvider {

    companion object {
        private val logger = logger<KotlinLineMarkerProvider>()
    }

    // Should have no-arg constructor
    @Suppress("UNUSED")
    constructor() : this(PostmanRequestGenerator())

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? {
        if (element !is KtNamedFunction) {
            return null
        }

        try {
            val annotationInfo = element.annotationEntries
                .mapNotNull { annotation ->
                    val annotationName = getAnnotationQualifiedName(annotation)
                    if (annotationName != null) {
                        AnnotationInfo(
                            qualifiedName = annotationName,
                            attributes = extractAnnotationAttributes(annotation)
                        )
                    } else null
                }

            val params = element.valueParameters.mapNotNull { param ->
                param.name?.let { ParameterInfo(it) }
            }

            val methodName = element.name
            if (methodName == null) {
                logger.warn("Method name is null")
                return null
            }

            val methodInfo = MethodInfo(
                name = methodName,
                project = element.project,
                parameters = params,
                annotations = annotationInfo
            )

            if (!postmanRequestGenerator.hasSupportedAnnotation(methodInfo)) {
                return null
            }

            val anchor = findAnchorElement(element)

            return LineMarkerInfo(
                anchor,
                anchor.textRange,
                IconHolder.ICON,
                { "Generate postman request" },
                { _, _ -> postmanRequestGenerator.generateJson(methodInfo) },
                GutterIconRenderer.Alignment.RIGHT,
                { "Generate postman request" }
            )

        } catch (e: Exception) {
            logger.warn("Error processing method ${element.name}", e)
            return null
        }
    }

    private fun getAnnotationQualifiedName(annotation: org.jetbrains.kotlin.psi.KtAnnotationEntry): String? {
        // Try to get the fully qualified name from the type reference
        val typeReference = annotation.typeReference
        val typeElement = typeReference?.typeElement

        // Get the annotation name from the constructor callee
        val constructorCallee = annotation.calleeExpression
        val annotationText = constructorCallee?.text

        return when {
            // If we have a fully qualified name in the source (e.g., org.springframework.web.bind.annotation.RequestMapping)
            annotationText?.contains(".") == true -> annotationText

            // If we have just the short name, try to resolve it
            annotationText != null -> {
                // Try to resolve through imports or use common mappings
                resolveAnnotationName(annotationText, annotation)
            }

            else -> null
        }
    }

    private fun resolveAnnotationName(shortName: String, annotation: org.jetbrains.kotlin.psi.KtAnnotationEntry): String {
        // Try to resolve through the containing file's imports
        val containingFile = annotation.containingKtFile
        val imports = containingFile.importDirectives

        // Look for an import that ends with the short name
        val matchingImport = imports.find { import ->
            import.importedFqName?.shortName()?.asString() == shortName
        }

        return matchingImport?.importedFqName?.asString() ?: run {
            // Fall back to common Spring annotation mappings
            when (shortName) {
                "RequestMapping" -> "org.springframework.web.bind.annotation.RequestMapping"
                "GetMapping" -> "org.springframework.web.bind.annotation.GetMapping"
                "PostMapping" -> "org.springframework.web.bind.annotation.PostMapping"
                "PutMapping" -> "org.springframework.web.bind.annotation.PutMapping"
                "DeleteMapping" -> "org.springframework.web.bind.annotation.DeleteMapping"
                "PatchMapping" -> "org.springframework.web.bind.annotation.PatchMapping"
                "RestController" -> "org.springframework.web.bind.annotation.RestController"
                "Controller" -> "org.springframework.stereotype.Controller"
                else -> shortName
            }
        }
    }

    fun extractAnnotationAttributes(
        annotation: org.jetbrains.kotlin.psi.KtAnnotationEntry
    ): Map<String, String> {
        val attributes = mutableMapOf<String, String>()

        annotation.valueArguments.forEach { arg ->
            val name = arg.getArgumentName()?.asName?.asString() ?: "value"
            val expression = arg.getArgumentExpression()

            when {
                expression?.text?.startsWith("\"") == true -> {
                    // String literal
                    attributes[name] = expression.text.removeSurrounding("\"")
                }
                expression?.text?.startsWith("arrayOf") == true -> {
                    // Array literal - extract content
                    val arrayContent = expression.text
                        .removePrefix("arrayOf(")
                        .removeSuffix(")")
                        .split(",")
                        .map { it.trim().removeSurrounding("\"") }
                    attributes[name] = arrayContent.toString()
                }
                expression != null -> {
                    // Other expressions (numbers, constants, etc.)
                    attributes[name] = expression.text
                }
            }
        }

        return attributes
    }

    private fun findAnchorElement(method: KtNamedFunction): PsiElement {
        // Use the function name identifier if available, otherwise the function itself
        return method.nameIdentifier ?: method
    }
}