package com.github.ferdistro.springpostmanrequestgenerator.toolwindow


import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import java.awt.Color

object Colors {

    private val colorScheme: EditorColorsScheme
        get() = EditorColorsManager.getInstance().schemeForCurrentUITheme

    val background: Color
        get() = colorScheme.defaultBackground

    val foreground: Color
        get() = colorScheme.defaultForeground

    val keyword: Color
        get() = colorScheme.getAttributes(DefaultLanguageHighlighterColors.KEYWORD).foregroundColor

    val string: Color
        get() = colorScheme.getAttributes(DefaultLanguageHighlighterColors.STRING).foregroundColor

    val number: Color
        get() = colorScheme.getAttributes(DefaultLanguageHighlighterColors.NUMBER).foregroundColor

    val lineComment: Color
        get() = colorScheme.getAttributes(DefaultLanguageHighlighterColors.LINE_COMMENT).foregroundColor

}
