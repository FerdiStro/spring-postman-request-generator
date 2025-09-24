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

    //todo: feature context mapping...
    override fun panelCenter(): JPanel {
        val panel = JPanel()
        return panel
    }

    override fun panelEnd(): JPanel {
        return JPanel()
    }
}