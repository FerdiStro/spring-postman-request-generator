package com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory

import com.github.ferdistro.springpostmanrequestgenerator.services.ConnectToPostmanApi
import com.github.ferdistro.springpostmanrequestgenerator.settings.RequestGeneratorSettings
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.JBUI
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

private const val DESCRIPTION_TEXT =
    "Enter your Postman API-Token to directly upload and update the generated collection to your default Postman workspace."
private const val DUMMY_STRING: String = "PMAK-XXXXXXXXXX-XXXXXXXXXXXXX"


class PostmanApiSectionPanelFactory : PanelFactory() {


    val dummy = if (!RequestGeneratorSettings.loadApiToken().isNullOrEmpty()) DUMMY_STRING else ""
    val apiToken = JPasswordField(dummy)


    val activate = JCheckBox("Activate Postman Collection").apply {
        isSelected = settings.state.apiActive
        addActionListener {
            apiToken.isEnabled = isSelected
            settings.state.apiActive = isSelected
            ApplicationManager.getApplication().saveSettings()
        }
    }


    override fun panelStart(): JPanel {
        return defaultHeader("Postman API Settings")
    }

    override fun panelName(): String {
        return "Postman API Section"
    }


    val apiTokenSave = JButton("Save API-TOKEN").apply {
        addActionListener {
            val token = String(apiToken.password)

            if (token != DUMMY_STRING) {


                if (ConnectToPostmanApi.verifyApiToken(token)) {
                    RequestGeneratorSettings.saveApiToken(token)
                    Messages.showErrorDialog("API-Token saved successfully", "Info")
                    return@addActionListener
                }

                Messages.showErrorDialog("Invalid API-Token", "Error")
                return@addActionListener
            }

            Messages.showErrorDialog("Please Enter a API-TOKEN", "Info")
        }
        apiToken.text = DUMMY_STRING

    }


    override fun panelCenter(): JPanel {
        val panel = JPanel(GridBagLayout())


        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(5)
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        }

        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 3
        panel.add(JLabel(DESCRIPTION_TEXT), gbc)


        gbc.gridx = 0
        gbc.gridy = 1
        gbc.gridwidth = 1
        gbc.weightx = 0.0
        panel.add(JLabel("API-TOKEN"), gbc)


        gbc.gridx = 1
        gbc.weightx = 1.0

        apiToken.isEnabled = activate.isSelected
        panel.add(apiToken, gbc)


        gbc.gridx = 2
        gbc.weightx = 0.0
        panel.add(apiTokenSave, gbc)

        gbc.gridx = 0
        gbc.gridy = 2
        gbc.gridwidth = 1
        gbc.weightx = 0.0
        panel.add(activate, gbc)

        return panel
    }

    override fun panelEnd(): JPanel {
        return JPanel()
    }


}