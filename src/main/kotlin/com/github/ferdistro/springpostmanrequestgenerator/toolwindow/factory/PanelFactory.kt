package com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory

import com.github.ferdistro.springpostmanrequestgenerator.settings.RequestGeneratorSettings
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.BorderFactory.createTitledBorder
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

abstract class PanelFactory {


    val settings: RequestGeneratorSettings
        get() = RequestGeneratorSettings.getInstance()


    abstract fun panelStart(): JPanel
    abstract fun panelCenter(): JPanel
    abstract fun panelEnd(): JPanel


    open fun panelName(): String {
        return ""
    }

    open fun createPanel(): JPanel {
        val panel = JPanel().apply {
            layout = BorderLayout()
            border = createTitledBorder(panelName())
            add(panelStart(), BorderLayout.PAGE_START)
            add(panelCenter(), BorderLayout.CENTER)
            add(panelEnd(), BorderLayout.PAGE_END)
        }
        return panel
    }

    fun defaultHeader(headline: String): JPanel {
        return JPanel(BorderLayout()).apply {
            add(JLabel(headline).apply {
                font = font.deriveFont(Font.BOLD, 15.0f)
                horizontalAlignment = SwingConstants.CENTER
            })
        }

    }
}