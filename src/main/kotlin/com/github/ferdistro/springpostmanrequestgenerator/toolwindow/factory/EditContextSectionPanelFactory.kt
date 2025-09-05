package com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory

import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

class EditContextSectionPanelFactory : PanelFactory() {


    private val ENV_BUTTON_TAG = "useEnv"


    override fun panelStart(): JPanel {
        return defaultHeader("Edit Context Section")
    }

    override fun panelName(): String {
        return "Edit Context Section"
    }

    val protocolCombo = JComboBox(arrayOf("http://", "https://"))
    val protocolEnvButton = JCheckBox(ENV_BUTTON_TAG)
    val serverTextField = JTextField(settings.state.serverUrl.value)
    val serverEnvButton = JCheckBox(ENV_BUTTON_TAG)
    val appContextTextField = JTextField(settings.state.context.value)
    val appContextEnvButton = JCheckBox(ENV_BUTTON_TAG)
    val disableCheckBox = JCheckBox("disable")

    override fun panelCenter(): JPanel {
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

        protocolEnvButton.isSelected = settings.state.protocol.useEnv
        addRow("Protocol", protocolCombo, null, protocolEnvButton, 0)

        if (settings.state.protocol.value == "https://") protocolCombo.selectedItem = settings.state.protocol.value

        serverEnvButton.isSelected = settings.state.serverUrl.useEnv
        addRow("Server", serverTextField, null, serverEnvButton, 1)


        appContextEnvButton.isSelected = settings.state.context.useEnv
        addRow("Application Context", appContextTextField, disableCheckBox, appContextEnvButton, 2)

        appContextTextField.isEnabled = !settings.state.context.extra
        disableCheckBox.isSelected = settings.state.context.extra

        disableCheckBox.addActionListener {
            appContextTextField.isEnabled = !disableCheckBox.isSelected && !appContextEnvButton.isSelected
        }


        return editSection
    }


    override fun panelEnd(): JPanel {
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

        return JPanel(BorderLayout()).apply {
            add(saveButton)
        }
    }


}