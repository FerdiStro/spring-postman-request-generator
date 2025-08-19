package com.github.ferdistro.springpostmanrequestgenerator.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class RequestGeneratorToolWindowFactory : ToolWindowFactory {


    @Override
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow) {

        val windowContent = ToolWindowContent(toolWindow)
        val content = ContentFactory.getInstance().createContent(windowContent.getContent(), "", false)
        toolWindow.contentManager.addContent(content)
    }
}