package com.github.ferdistro.springpostmanrequestgenerator.toolwindow

import com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory.EditContextSectionPanelFactory
import com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory.EditMappingSectionFactory
import com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory.PanelFactory
import com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory.PostmanApiSectionPanelFactory
import com.intellij.ide.BrowserUtil
import org.jdesktop.swingx.JXHyperlink
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.Font
import javax.swing.*


class ToolWindowContent : PanelFactory() {


    val factoryList: List<PanelFactory> = listOf(
        EditContextSectionPanelFactory(),
        EditMappingSectionFactory(),
        PostmanApiSectionPanelFactory(),
    )

    override fun createPanel(): JPanel {
        val contentPanel = JPanel()

        contentPanel.setLayout(BorderLayout(0, 0))
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0))

        contentPanel.add(panelStart(), BorderLayout.PAGE_START)
        contentPanel.add(panelCenter(), BorderLayout.CENTER)

        return contentPanel
    }


    override fun panelStart(): JPanel {
        val panel = JPanel(BorderLayout())
        val headline = JLabel(
            "Spring Postman Request Generator", SwingConstants.CENTER
        ).apply {
            horizontalTextPosition = SwingConstants.RIGHT
            iconTextGap = 12
            font = font.deriveFont(Font.BOLD, 24.0f)
            horizontalAlignment = SwingConstants.CENTER
        }
        panel.add(headline, BorderLayout.PAGE_START)


        val docLabel = JLabel("Check the Toolwindow Documentation for more informations. ")
        val jxHyperlink = JXHyperlink().apply {
            text = "Documentation"
            toolTipText = "Open online documentation"
            clickedColor = Color(128, 0, 128)
            addActionListener {
                BrowserUtil.browse("https://github.com/FerdiStro/spring-postman-request-generator/tree/main/doc/Toolwindow.md")
            }
        }
        val innerPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            add(docLabel)
            add(jxHyperlink)
        }

        panel.add(innerPanel, BorderLayout.CENTER)
        return panel
    }


    override fun panelCenter(): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            factoryList.forEach {

                add(it.createPanel().apply {
                    maximumSize = java.awt.Dimension(Int.MAX_VALUE, preferredSize.height)
                })
                add(Box.createVerticalStrut(20))
            }
        }
    }

    override fun panelEnd(): JPanel {
        return JPanel()
    }
}

