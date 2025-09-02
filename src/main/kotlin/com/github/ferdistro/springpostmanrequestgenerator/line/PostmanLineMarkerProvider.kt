package com.github.ferdistro.springpostmanrequestgenerator.line

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.psi.PsiElement

interface PostmanLineMarkerProvider: LineMarkerProvider{
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>?
}
