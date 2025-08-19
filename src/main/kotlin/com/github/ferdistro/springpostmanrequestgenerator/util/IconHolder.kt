package com.github.ferdistro.springpostmanrequestgenerator.util

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object IconHolder {
    val ICON: Icon = IconLoader.getIcon("/META-INF/icon.svg", IconHolder::class.java)
}