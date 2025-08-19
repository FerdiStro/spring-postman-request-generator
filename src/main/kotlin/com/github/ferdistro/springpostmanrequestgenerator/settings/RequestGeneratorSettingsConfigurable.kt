package com.github.ferdistro.springpostmanrequestgenerator.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.NlsContexts
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField


class RequestGeneratorSettingsConfigurable : Configurable {

    private var panel: JPanel? = null
    private val serverField = JTextField()


    override fun getDisplayName(): @NlsContexts.ConfigurableName String? {
        TODO("Not yet implemented")
    }

    override fun createComponent(): JComponent? {
        TODO("Not yet implemented")
    }

    override fun isModified(): Boolean {
        TODO("Not yet implemented")
    }

    override fun apply() {
        TODO("Not yet implemented")
    }
}