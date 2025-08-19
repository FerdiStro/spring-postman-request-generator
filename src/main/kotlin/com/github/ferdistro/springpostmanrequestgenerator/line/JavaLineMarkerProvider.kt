package com.github.ferdistro.springpostmanrequestgenerator.line

import com.github.ferdistro.springpostmanrequestgenerator.services.PostmanRequestGenerator
import com.github.ferdistro.springpostmanrequestgenerator.util.IconHolder
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiTypeElement
import java.awt.event.MouseEvent

class JavaLineMarkerProvider(
    private val postmanRequestGenerator: PostmanRequestGenerator,
) : PostmanLineMarkerProvider {

    //should have no-arg constructor
    @Suppress("UNUSED")
    constructor() : this(PostmanRequestGenerator())

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? {
        if (element !is PsiMethod) return null

        if (postmanRequestGenerator.hasSupportedAnnotation(element).not()) return null

        val anchor = findAnchorElement(element)

        return LineMarkerInfo(
            /* element = */ anchor,
            /* range = */ anchor.textRange,
            /* icon = */ IconHolder.ICON,
            /* tooltipProvider = */ { "Generate JSON" },
            /* navHandler = */ { _: MouseEvent?, _: Any? -> postmanRequestGenerator.generateJson(element) },
            /* alignment = */ GutterIconRenderer.Alignment.RIGHT,
            /* accessibleNameProvider = */ { "Generate JSON" }
        )
    }

    private fun findAnchorElement(method: PsiMethod): PsiElement {
        val anchor = method.modifierList.nextSibling ?: method
        return anchor as? PsiTypeElement ?: (method.nameIdentifier ?: method)
    }
}