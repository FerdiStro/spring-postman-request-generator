package com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory

import com.github.ferdistro.springpostmanrequestgenerator.util.Colors
import com.github.ferdistro.springpostmanrequestgenerator.util.UIUtils
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

private const val ENV_BUTTON_TAG = "useEnv"


class EditContextSectionPanelFactory : PanelFactory() {


    override fun panelStart(): JPanel {
        return UIUtils.defaultHeader("Context Settings")
    }

    override fun panelName(): String {
        return "Edit Context Section"
    }

    val saveButton = UIUtils.defaultButton("Save")
    val protocolCombo = JComboBox(arrayOf("http://", "https://"))
    val protocolEnvButton = JCheckBox(ENV_BUTTON_TAG)
    val serverTextField = JTextField(settings.state.serverUrl.value)
    val serverEnvButton = JCheckBox(ENV_BUTTON_TAG)
    val appContextTextField = JTextField(settings.state.context.value)
    val appContextEnvButton = JCheckBox(ENV_BUTTON_TAG)
    val disableCheckBox = JCheckBox("disable")

    private fun changeTextFieldColor(field: JTextField) {
        field.background = UIManager.getColor("ComboBox.background")
        field.foreground = UIManager.getColor("ComboBox.foreground")
        field.border = UIManager.getBorder("ComboBox.border")
        field.font = UIManager.getFont("ComboBox.font")
        field.border = UIManager.getBorder("TextField.border")
        field.disabledTextColor = UIManager.getColor("ComboBox.disabledForeground")

        field.background = if (serverTextField.isEnabled) {
            UIManager.getColor("ComboBox.background")
        } else {
            UIManager.getColor("ComboBox.disabledBackground")
        }
    }

    override fun panelCenter(): JPanel {
        val editSection = object : JPanel(GridBagLayout()) {
            override fun paintComponent(g: java.awt.Graphics?) {
                changeTextFieldColor(appContextTextField)
                changeTextFieldColor(serverTextField)
                saveButton.foreground = Colors.keyword
                saveButton.border = UIUtils.defaultButton("").border
            }
        }

        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(5)
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        }

        fun addRow(
            labelText: String, field: JComponent, extra: JComponent? = null, defaultOption: JCheckBox, row: Int
        ) {
            gbc.gridx = 0
            gbc.weightx = 0.0
            gbc.gridy = row
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

        protocolEnvButton.isSelected = settings.state.protocol.useEnv
        addRow("Protocol", protocolCombo, null, protocolEnvButton, 0)

        if (settings.state.protocol.value == "https://") protocolCombo.selectedItem = settings.state.protocol.value

        serverEnvButton.isSelected = settings.state.serverUrl.useEnv
        addRow("Server", serverTextField, null, serverEnvButton, 1)


        appContextEnvButton.isSelected = settings.state.context.useEnv || settings.state.context.extra
        addRow("Application Context", appContextTextField, disableCheckBox, appContextEnvButton, 2)

        appContextTextField.isEnabled = !settings.state.context.extra


        disableCheckBox.isSelected = settings.state.context.extra

        disableCheckBox.addActionListener {
            appContextTextField.isEnabled = !disableCheckBox.isSelected && !appContextEnvButton.isSelected
        }


        return editSection
    }


    override fun panelEnd(): JPanel {

        saveButton.addActionListener {

            settings.state.serverUrl.value = serverTextField.text
            settings.state.serverUrl.useEnv = serverEnvButton.isSelected

            settings.state.protocol.value = protocolCombo.selectedItem as String
            settings.state.protocol.useEnv = protocolEnvButton.isSelected

            settings.state.context.value = appContextTextField.text
            settings.state.context.extra = disableCheckBox.isSelected
            settings.state.context.useEnv = appContextEnvButton.isSelected

            ApplicationManager.getApplication().saveSettings()
            Messages.showInfoMessage("Context Settings saved! ", "Success")

        }

        return JPanel(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
            add(saveButton)
        }
    }


}