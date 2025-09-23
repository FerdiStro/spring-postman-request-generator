package com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory

import com.github.ferdistro.springpostmanrequestgenerator.settings.RequestGeneratorSettings
import com.github.ferdistro.springpostmanrequestgenerator.toolwindow.Colors
import com.intellij.ide.BrowserUtil
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import org.jdesktop.swingx.JXHyperlink
import java.awt.*
import javax.swing.BorderFactory
import javax.swing.BorderFactory.createTitledBorder
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.border.CompoundBorder


abstract class PanelFactory {


    /**
     * Util-class for generating doc text with hyperlink to the doc
     */
    protected fun docPanel(description: String, urlAsString: String): JPanel {
        val docPanel = JPanel(GridBagLayout()).apply {
            isOpaque = false
        }
        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(2)
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
            gridy = 0
        }

        gbc.gridx = 0
        val docText = JBTextArea(description).apply {
            lineWrap = true
            wrapStyleWord = true
            isEditable = false
            isOpaque = false
        }
        docPanel.add(docText, gbc)

        gbc.gridx = 0
        gbc.gridy = 1
        gbc.weightx = 0.0
        val jxHyperlink = JXHyperlink().apply {
            text = "here"
            toolTipText = "Hyperlink for documentation. Click to open the documentation in your browser."
            clickedColor = Color(128, 0, 128)
            addActionListener {
                BrowserUtil.browse(urlAsString)
            }
        }
        docPanel.add(jxHyperlink, gbc)


        return docPanel
    }

    open fun extraSpace(): Int {
        return 0
    }

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

    fun defaultHeader(headline: String): JPanel {
        return JPanel(BorderLayout()).apply {
            add(object : JLabel(headline) {
                override fun paintComponent(g: Graphics) {
                    foreground = Colors.number
                    super.paintComponent(g)
                }
            }.apply {
                font = font.deriveFont(Font.BOLD, 15.0f)
                horizontalAlignment = SwingConstants.CENTER
            })
        }

    }
}