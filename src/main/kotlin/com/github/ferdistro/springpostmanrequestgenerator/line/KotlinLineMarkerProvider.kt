package com.github.ferdistro.springpostmanrequestgenerator.line

import com.github.ferdistro.springpostmanrequestgenerator.services.PostmanRequestGenerator
import com.github.ferdistro.springpostmanrequestgenerator.util.IconHolder
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.asJava.elements.KtLightMethod
import org.jetbrains.kotlin.idea.caches.resolve.analyzeInContext
import org.jetbrains.kotlin.idea.codeInsight.lineMarkers.shared.expectOrActualAnchor
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.awt.event.MouseEvent


class KotlinLineMarkerProvider(
    private val postmanRequestGenerator: PostmanRequestGenerator,
) : PostmanLineMarkerProvider {

    //should have no-arg constructor
    @Suppress("UNUSED")
    constructor() : this(PostmanRequestGenerator())

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? {
        if (element !is PsiMethod) return null

        if (postmanRequestGenerator.hasSupportedAnnotation(element).not()) return null

        if (element !is KtLightMethod) return null

        return LineMarkerInfo(
            /* element = */ element,
            /* range = */ element.textRange,
            /* icon = */ IconHolder.ICON,
            /* tooltipProvider = */ { "Generate JSON" },
            /* navHandler = */ { _: MouseEvent?, _: Any? -> postmanRequestGenerator.generateJson(element) },
            /* alignment = */ GutterIconRenderer.Alignment.RIGHT,
            /* accessibleNameProvider = */ { "Generate JSON" }
        )
    }

}