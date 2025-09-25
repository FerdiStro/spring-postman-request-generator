package com.github.ferdistro.springpostmanrequestgenerator.util

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import java.awt.Component
import java.awt.FlowLayout
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.TreeCellRenderer

data class GroupData(val name: String, var enabled: Boolean)

class CheckBoxTreeTableNode(userObject: GroupData) : DefaultMutableTreeTableNode(userObject)

class CheckBoxTreeCellRenderer : JPanel(), TreeCellRenderer {
    private val checkBox = JCheckBox()
    private val label = JLabel()

    init {
        layout = FlowLayout(FlowLayout.LEFT, 2, 0)
        isOpaque = false
        add(checkBox)
        add(label)
    }



    override fun getTreeCellRendererComponent(
        tree: JTree?, value: Any?, selected: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean
    ): Component {
        checkBox.isVisible = true
        if (value is DefaultMutableTreeTableNode) {
            val userObj = value.userObject
            when (userObj) {
                is GroupData -> {
                    checkBox.isVisible = true
                    checkBox.isSelected = userObj.enabled
                    label.text = userObj.name
                }

                is Array<*> -> {
                    checkBox.isVisible = false
                    label.text = userObj[0].toString()
                }

                else -> {
                    checkBox.isVisible = false
                    label.text = userObj?.toString() ?: ""
                }
            }
        }
        return this
    }
}