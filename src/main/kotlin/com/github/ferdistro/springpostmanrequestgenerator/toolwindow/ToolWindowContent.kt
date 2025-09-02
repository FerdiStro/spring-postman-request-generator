package com.github.ferdistro.springpostmanrequestgenerator.toolwindow

import com.github.ferdistro.springpostmanrequestgenerator.settings.RequestGeneratorSettings
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.util.ui.JBUI
import org.jdesktop.swingx.JXHyperlink
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingConstants


class ToolWindowContent {

    private val contentPanel = JPanel()

    private val settings = RequestGeneratorSettings.getInstance()
    private val ENV_BUTTON_TAG = "useEnv"

    fun getContent(): JPanel = contentPanel

    constructor(toolWindow: ToolWindow) {


        contentPanel.setLayout(BorderLayout(0, 0))
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0))
        contentPanel.add(createHeadlinePanel(), BorderLayout.PAGE_START)

        val wrapper = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
        wrapper.add(createContextPanel())
        contentPanel.add(wrapper, BorderLayout.LINE_START)

    }

    /**
     * todo:
     * Create Mapping Editor Section
     */


    /**
     * Create Context Editor Section
     */
    private fun createContextPanel(): JPanel {
        val panel = JPanel(BorderLayout())
        panel.border = BorderFactory.createTitledBorder("Edit Context Section:")

        // Headline
        val headline = JLabel("Context Settings").apply {
            font = font.deriveFont(Font.BOLD, 15.0f)
            horizontalAlignment = SwingConstants.CENTER
        }
        panel.add(headline, BorderLayout.PAGE_START)

        // Edit Section
        val editSection = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(5)
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        }

        fun addRow(
            labelText: String,
            field: JComponent,
            extra: JComponent? = null,
            defaultOption: JCheckBox,
            row: Int
        ) {
            gbc.gridx = 0
            gbc.gridy = row
            gbc.weightx = 0.0
            editSection.add(JLabel(labelText), gbc)

            gbc.gridx = 1
            gbc.weightx = 1.0
            editSection.add(field, gbc)
            field.isEnabled = !defaultOption.isSelected

            gbc.gridx = 2
            gbc.weightx = 0.0
            editSection.add(defaultOption, gbc)

            if (extra != null) {
                gbc.gridx = 3
                gbc.weightx = 0.0
                editSection.add(extra, gbc)
            }

            defaultOption.addActionListener {
                field.isEnabled = !defaultOption.isSelected
            }

        }

        val protocolCombo = JComboBox(arrayOf("http://", "https://"))
        val protocolEnvButton = JCheckBox(ENV_BUTTON_TAG)
        protocolEnvButton.isSelected = settings.state.protocol.useEnv
        addRow("Protocol", protocolCombo, null, protocolEnvButton, 0)

        if (settings.state.protocol.value == "https://") protocolCombo.selectedItem = settings.state.protocol.value


        val serverTextField = JTextField(settings.state.serverUrl.value)
        val serverEnvButton = JCheckBox(ENV_BUTTON_TAG)
        serverEnvButton.isSelected = settings.state.serverUrl.useEnv
        addRow("Server", serverTextField, null, serverEnvButton, 1)

        val appContextTextField = JTextField(settings.state.context.value)
        val appContextEnvButton = JCheckBox(ENV_BUTTON_TAG)
        appContextEnvButton.isSelected = settings.state.context.useEnv
        val disableCheckBox = JCheckBox("disable")
        addRow("Application Context", appContextTextField, disableCheckBox, appContextEnvButton, 2)

        appContextTextField.isEnabled = !settings.state.context.extra
        disableCheckBox.isSelected = settings.state.context.extra

        disableCheckBox.addActionListener {
            appContextTextField.isEnabled = !disableCheckBox.isSelected && !appContextEnvButton.isSelected
        }
        panel.add(editSection, BorderLayout.CENTER)

        val saveButton = JButton("Save")
        saveButton.addActionListener {

            settings.state.serverUrl.value = serverTextField.text
            settings.state.serverUrl.useEnv = serverEnvButton.isSelected

            settings.state.protocol.value = protocolCombo.selectedItem as String
            settings.state.protocol.useEnv = protocolEnvButton.isSelected

            settings.state.context.value = appContextTextField.text
            settings.state.context.extra = disableCheckBox.isSelected
            settings.state.context.useEnv = appContextEnvButton.isSelected

            ApplicationManager.getApplication().saveSettings()
        }
        panel.add(saveButton, BorderLayout.PAGE_END)
        return panel
    }

    /**
     * Create Headline JPanel (Ordered to Page_Start)
     * Doc: https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html
     */
    fun createHeadlinePanel(): JPanel {
        val panel = JPanel(BorderLayout())
        val headline = JLabel(
            "Spring Postman Request Generator",
            SwingConstants.CENTER
        )
            .apply {
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
}

