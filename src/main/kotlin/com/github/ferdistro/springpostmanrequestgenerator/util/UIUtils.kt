package com.github.ferdistro.springpostmanrequestgenerator.util

import com.intellij.ide.BrowserUtil
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import org.jdesktop.swingx.JXHyperlink
import java.awt.*
import javax.swing.*

object UIUtils {

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

    fun defaultButton(buttonName: String): JButton {
        val saveButton: JButton = JButton(buttonName).apply {
            cursor = Cursor(Cursor.HAND_CURSOR)
            border = BorderFactory.createLineBorder(Colors.keyword, 2, true)
            foreground = Colors.keyword
        }
        return saveButton
    }


    fun docPanel(description: String, urlAsString: String): JPanel {
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
            text = "here \uD83D\uDD17"
            toolTipText = "Hyperlink for documentation. Click to open the documentation in your browser."
            clickedColor = Color(128, 0, 128)
            addActionListener {
                BrowserUtil.browse(urlAsString)
            }
        }
        docPanel.add(jxHyperlink, gbc)


        return docPanel
    }
}