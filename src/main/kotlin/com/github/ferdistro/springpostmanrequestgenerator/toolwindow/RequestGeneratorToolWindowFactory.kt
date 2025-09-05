package com.github.ferdistro.springpostmanrequestgenerator.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class RequestGeneratorToolWindowFactory : ToolWindowFactory, DumbAware {


    @Override
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content = ContentFactory.getInstance().createContent(ToolWindowContent().createPanel(), "", false)
        toolWindow.contentManager.addContent(content)
    }
}