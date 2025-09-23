package com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory

import com.github.ferdistro.springpostmanrequestgenerator.util.Colors
import com.github.ferdistro.springpostmanrequestgenerator.util.UIUtils
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.Messages
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*


class GenerellSettingsSectionFactory : PanelFactory() {
    val openAfterGenerate: JCheckBox = JCheckBox()
    val collectionName = JTextField(settings.state.general.collectionName)
    val saveButton = UIUtils.defaultButton("Save")

    override fun panelStart(): JPanel {
        return UIUtils.defaultHeader("Generell Settings")
    }

    override fun panelName(): String {
        return "Generell Settings Section"
    }

    override fun panelCenter(): JPanel {
        val settingsPanel = object : JPanel(GridBagLayout()) {
            override fun paintComponent(g: java.awt.Graphics?) {
                saveButton.foreground = Colors.keyword
                saveButton.border = UIUtils.defaultButton("").border
            }
        }


        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
        }


        gbc.gridx = 0
        gbc.weightx = 0.0
        gbc.gridy = 0


        //Collection-Name
        settingsPanel.add(JLabel("Collection-Name"), gbc)

        gbc.gridx = 1
        gbc.weightx = 1.0
        settingsPanel.add(collectionName, gbc)

        //Open after generation
        gbc.gridy = 1

        gbc.gridx = 0
        gbc.weightx = 0.0
        settingsPanel.add(JLabel("Open .json after generation"), gbc)

        gbc.gridx = 1
        gbc.weightx = 1.0
        openAfterGenerate.isSelected = settings.state.general.openAfterGeneration
        settingsPanel.add(openAfterGenerate, gbc)


        return settingsPanel
    }

    override fun panelEnd(): JPanel {

        saveButton.addActionListener {
            settings.state.general.collectionName = collectionName.text
            settings.state.general.openAfterGeneration = openAfterGenerate.isSelected
            ApplicationManager.getApplication().saveSettings()
            Messages.showInfoMessage("Context Settings saved! ", "Success")
        }
        return JPanel(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
            add(saveButton)
        }
    }

}