package com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory

import com.github.ferdistro.springpostmanrequestgenerator.settings.RequestGeneratorSettings
import com.github.ferdistro.springpostmanrequestgenerator.util.Colors
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Graphics
import javax.swing.BorderFactory
import javax.swing.BorderFactory.createTitledBorder
import javax.swing.JPanel
import javax.swing.border.CompoundBorder


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

        val panel = object : JPanel() {

            @Override
            override fun paintComponent(g: Graphics) {
                foreground = Colors.foreground
                super.paintComponent(g)
            }

        }.apply {
            border = object :
                CompoundBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20), createTitledBorder(panelName())) {

                @Override
                override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
                    (insideBorder as? javax.swing.border.TitledBorder)?.titleColor = Colors.keyword
                    super.paintBorder(c, g, x, y, width, height)
                }
            }
            layout = BorderLayout()

            val panelStart = panelStart()
            val panelCenter = panelCenter()
            val panelEnd = panelEnd()


            add(panelStart, BorderLayout.PAGE_START)
            add(panelCenter, BorderLayout.CENTER)
            add(panelEnd, BorderLayout.PAGE_END)
        }
        return panel
    }


}