package com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory

import javax.swing.JPanel

class EditMappingSectionFactory : PanelFactory() {
    override fun panelStart(): JPanel {
        return defaultHeader("Edit Mapping Section")
    }

    override fun panelName(): String {
        return "Edit Mapping Section"
    }

    //todo: feature context mapping...
    override fun panelCenter(): JPanel {
        val panel = JPanel()
        panel.add(defaultHeader("Planed for Version >= 0.7.0 "))
        return panel
    }

    override fun panelEnd(): JPanel {
        return JPanel()
    }
}