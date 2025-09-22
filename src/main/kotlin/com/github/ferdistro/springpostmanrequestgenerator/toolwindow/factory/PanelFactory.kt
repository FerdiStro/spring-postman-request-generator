package com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory

import com.github.ferdistro.springpostmanrequestgenerator.settings.RequestGeneratorSettings
import com.github.ferdistro.springpostmanrequestgenerator.toolwindow.Colors
import java.awt.BorderLayout
import java.awt.Font
import java.awt.Graphics
import javax.swing.BorderFactory
import javax.swing.BorderFactory.createTitledBorder
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.border.Border


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
        val panel = object : JPanel(){
            override fun paintComponent(g: Graphics) {
                background = Colors.background
                foreground = Colors.foreground
                super.paintComponent(g)
            }
        }.apply {


            layout = BorderLayout()
            border = createTitledBorder(panelName()).apply {
                titleColor = Colors.number
                background = Colors.background
            }

            add(panelStart(), BorderLayout.PAGE_START)
            add(panelCenter(), BorderLayout.CENTER)
            add(panelEnd(), BorderLayout.PAGE_END)
        }

        return panel
    }

    fun defaultHeader(headline: String): JPanel {
        return JPanel(BorderLayout()).apply {
            add(
                object : JLabel(headline){
                    override fun paintComponent(g: Graphics) {
                        background = Colors.background
                        foreground = Colors.number
                        super.paintComponent(g)
                    }
                }.
                apply {
                    font = font.deriveFont(Font.BOLD, 15.0f)
                    horizontalAlignment = SwingConstants.CENTER
                })
        }

    }
}