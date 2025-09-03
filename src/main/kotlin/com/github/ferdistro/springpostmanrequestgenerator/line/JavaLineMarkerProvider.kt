package com.github.ferdistro.springpostmanrequestgenerator.line

import com.github.ferdistro.springpostmanrequestgenerator.AnnotationInfo
import com.github.ferdistro.springpostmanrequestgenerator.MethodInfo
import com.github.ferdistro.springpostmanrequestgenerator.ParameterInfo
import com.github.ferdistro.springpostmanrequestgenerator.services.PostmanRequestGenerator
import com.github.ferdistro.springpostmanrequestgenerator.util.IconHolder
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiTypeElement
import com.intellij.psi.impl.source.tree.java.PsiNameValuePairImpl
import org.jetbrains.kotlin.asJava.unwrapped
import java.awt.event.MouseEvent

class JavaLineMarkerProvider(
    private val postmanRequestGenerator: PostmanRequestGenerator,
) : PostmanLineMarkerProvider {

    companion object {
        private val logger = logger<JavaLineMarkerProvider>()
    }

    //should have no-arg constructor
    @Suppress("UNUSED")
    constructor() : this(PostmanRequestGenerator())


    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? {
        if (element !is PsiMethod) return null
        val annotationInfo = element.annotations
            .map { it ->
                AnnotationInfo(
                    qualifiedName = it.qualifiedName!!,
                    attributes = it.attributes.mapNotNull { attribute ->

                        val name = attribute.attributeName

                        var value = when (val expr = attribute) {
                            is PsiNameValuePairImpl -> expr.value?.unwrapped?.text ?: ""
                            else -> ""
                        }
                        value = value.replace("\"", "")
                        value.let { v -> name to v }
                    }.toMap()
                )
            }

        val params = element
            .parameters.map {
                ParameterInfo(
                    it.name!!
                )
            }

        val methodInfo = MethodInfo(
            name = element.name,
            project = element.project,
            parameters = params,
            annotations = annotationInfo,
        )

        if (postmanRequestGenerator.hasSupportedAnnotation(methodInfo).not()) return null

        val anchor = findAnchorElement(element)

        val handler = { _: MouseEvent?, _: PsiElement ->
            //check(param is PsiMethod)
            //postmanRequestGenerator.generateJson(param)
            postmanRequestGenerator.generateJson(methodInfo)
        }

        return LineMarkerInfo(
            /* element = */ anchor,
            /* range = */ anchor.textRange,
            /* icon = */ IconHolder.ICON,
            /* tooltipProvider = */ { "Generate postman request" },
            /* navHandler = */ handler,
            /* alignment = */ GutterIconRenderer.Alignment.RIGHT,
            /* accessibleNameProvider = */ { "Generate postman request" }
        )
    }

    private fun findAnchorElement(method: PsiMethod): PsiElement {
        val ns = method.modifierList.nextSibling
        val anchor = ns ?: method
        val elem = anchor as? PsiTypeElement
        val other = method.nameIdentifier
        return elem ?: other ?: method
    }
}