package com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory

import com.github.ferdistro.springpostmanrequestgenerator.services.ConnectToPostmanApi
import com.github.ferdistro.springpostmanrequestgenerator.settings.RequestGeneratorSettings
import com.github.ferdistro.springpostmanrequestgenerator.util.UIUtils
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.Messages
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*


private const val DOC_TEXT =
    "Enter your Postman API-Token to directly upload and update the generated collection to your default Postman workspace. Generate yourt own postman API-Token"
private const val API_TOKEN_URL = "https://go.postman.co/settings/me/api-keys"
private const val DUMMY_STRING: String = "PMAK-XXXXXXXXXX-XXXXXXXXXXXXX"


class PostmanApiSectionPanelFactory : PanelFactory() {


    val dummy = if (!RequestGeneratorSettings.loadApiToken().isNullOrEmpty()) DUMMY_STRING else ""
    val apiToken = JPasswordField(dummy)


    val activate = JCheckBox("Activate Postman Collection").apply {
        isSelected = settings.state.apiActive
        addActionListener {
            apiToken.isEnabled = isSelected
            apiTokenSave.isEnabled = isSelected
            settings.state.apiActive = isSelected
            ApplicationManager.getApplication().saveSettings()
        }
    }


    override fun panelStart(): JPanel {
        return UIUtils.defaultHeader("Postman API Settings")
    }

    override fun panelName(): String {
        return "Postman API Section"
    }


    val apiTokenSave = JButton("Save API-TOKEN").apply {
        addActionListener {
            val token = String(apiToken.password)

            if (token != DUMMY_STRING) {
                val postmanApi = com.intellij.openapi.project.ProjectManager.getInstance().defaultProject.getService(
                    ConnectToPostmanApi::class.java)

                if (postmanApi.verifyApiToken(token)) {
                    RequestGeneratorSettings.saveApiToken(token)
                    Messages.showInfoMessage("API-Token saved successfully", "Info")
                    return@addActionListener
                }

                Messages.showErrorDialog("Invalid API-Token", "Error")
                return@addActionListener
            }

            Messages.showErrorDialog("Please Enter a API-TOKEN in the InputField", "No Token provided")
        }
        apiToken.text = DUMMY_STRING

    }


    override fun panelCenter(): JPanel {
        val panel = JPanel(GridBagLayout())

        val gbc = GridBagConstraints()

        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 3
        gbc.weighty = 1.0
        gbc.weightx = 1.0
        gbc.fill = GridBagConstraints.BOTH

        val doc = UIUtils.docPanel(DOC_TEXT, API_TOKEN_URL)
        panel.add(doc, gbc)

        //API-Token Text
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weighty = 0.0
        gbc.gridy = 1

        gbc.gridx = 0
        gbc.gridwidth = 1
        gbc.weightx = 0.0
        panel.add(JLabel("API-TOKEN"), gbc)


        //API-Token input field
        apiToken.isEnabled = activate.isSelected

        gbc.gridx = 1
        gbc.gridwidth = 1
        gbc.weightx = 1.0
        panel.add(apiToken, gbc)


        //Save API-TOKEN button
        apiTokenSave.isEnabled = activate.isSelected

        gbc.gridx = 2
        gbc.gridwidth = 1
        gbc.weightx = 0.0
        panel.add(apiTokenSave, gbc)


        //Activate Checkbox
        gbc.gridx = 0
        gbc.gridy = 2
        gbc.gridwidth = 3
        gbc.weightx = 0.0
        panel.add(activate, gbc)

        return panel
    }

    override fun panelEnd(): JPanel {
        return JPanel()
    }


}