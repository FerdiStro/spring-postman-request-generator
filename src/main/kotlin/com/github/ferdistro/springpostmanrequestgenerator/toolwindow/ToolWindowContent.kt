package com.github.ferdistro.springpostmanrequestgenerator.toolwindow

import com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory.*
import com.github.ferdistro.springpostmanrequestgenerator.util.Colors
import com.github.ferdistro.springpostmanrequestgenerator.util.UIUtils
import java.awt.*
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

private const val TOP_TEXT = "Check the Toolwindow Documentation for more information's "
private const val TOOLWINDOW_DOC_URL =
    "https://github.com/FerdiStro/spring-postman-request-generator/tree/main/doc/Toolwindow.md"

class ToolWindowContent : PanelFactory() {


    val factoryList: List<PanelFactory> = listOf(
        GenerellSettingsSectionFactory(),
        EditContextSectionPanelFactory(),
        EditMappingSectionFactory(),
        PostmanApiSectionPanelFactory(),
    )

    override fun createPanel(): JPanel {
        return object : JPanel() {
            override fun paintComponent(g: Graphics) {
                background = Colors.background
                super.paintComponent(g)
            }
        }.apply {
            maximumSize = Dimension(Int.MAX_VALUE, Int.MAX_VALUE)
            layout = BorderLayout(0, 0)
            add(panelStart(), BorderLayout.PAGE_START)
            add(panelCenter(), BorderLayout.CENTER)
        }
    }


    override fun panelStart(): JPanel {
        val panelStart = JPanel(BorderLayout())

        val headline = object : JLabel(
            "Spring Postman Request Generator", CENTER
        ) {
            override fun paintComponent(g: Graphics) {
                foreground = Colors.keyword
                super.paintComponent(g)
            }
        }.apply {
            horizontalTextPosition = SwingConstants.RIGHT
            iconTextGap = 12
            font = font.deriveFont(Font.BOLD, 24.0f)
            horizontalAlignment = SwingConstants.CENTER
        }
        panelStart.add(headline, BorderLayout.PAGE_START)


        val docPanel = UIUtils.docPanel(TOP_TEXT, TOOLWINDOW_DOC_URL)
        panelStart.add(docPanel, BorderLayout.CENTER)

        return panelStart
    }


    override fun panelCenter(): JPanel {
        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.BOTH
            weightx = 1.0
            weighty = 1.0
        }
        return JPanel(GridBagLayout()).apply {
            for (i in factoryList.indices) {
                gbc.gridy = i
                val createPanel = factoryList[i].createPanel()
                add(createPanel, gbc)
            }
        }
    }

    override fun panelEnd(): JPanel {
        return JPanel()
    }


}

