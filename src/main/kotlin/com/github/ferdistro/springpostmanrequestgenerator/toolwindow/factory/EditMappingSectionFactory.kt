package com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory

import com.github.ferdistro.springpostmanrequestgenerator.util.UIUtils
import javax.swing.JPanel

class EditMappingSectionFactory : PanelFactory() {

    override fun panelStart(): JPanel {
        return UIUtils.defaultHeader("Mapping Settings")
    }

    override fun panelName(): String {
        return "Edit Mapping Section"
    }

    override fun panelCenter(): JPanel {
        val panel = JPanel()
        panel.add(UIUtils.defaultHeader("Planed for Version >= 0.7.0 "))
        return panel
    }

    override fun panelEnd(): JPanel {
        return JPanel()
    }
}