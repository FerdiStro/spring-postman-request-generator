package com.github.ferdistro.springpostmanrequestgenerator.toolwindow.factory

import com.github.ferdistro.springpostmanrequestgenerator.util.CheckBoxTreeCellRenderer
import com.github.ferdistro.springpostmanrequestgenerator.util.CheckBoxTreeTableNode
import com.github.ferdistro.springpostmanrequestgenerator.util.GroupData
import com.github.ferdistro.springpostmanrequestgenerator.util.UIUtils
import org.jdesktop.swingx.JXTreeTable
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import org.jdesktop.swingx.treetable.DefaultTreeTableModel
import org.jdesktop.swingx.treetable.TreeTableNode
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane

class EditMappingSectionFactory : PanelFactory() {

    override fun panelStart(): JPanel {
        return UIUtils.defaultHeader("Mapping Settings")
    }

    override fun panelName(): String {
        return "Edit Mapping Section"
    }

    override fun panelCenter(): JPanel {
        val root = DefaultMutableTreeTableNode("Root")


        val groupNode = CheckBoxTreeTableNode(GroupData("Test", false))

        groupNode.isEditable(1)
        groupNode.isEditable(0)
        root.add(groupNode)



        groupNode.add(DefaultMutableTreeTableNode(arrayOf("*POST /TAPI/...", "foo1", "foo2", "REMOVE")))
        groupNode.add(DefaultMutableTreeTableNode(arrayOf("*stockList.go.*", "foo3", "foo4", "REMOVE")))
        groupNode.add(DefaultMutableTreeTableNode(arrayOf("*GET /TAPI/C...", "foo5", "foo6", "REMOVE")))
        groupNode.add(DefaultMutableTreeTableNode(arrayOf("*/advisorfees*", "foo7", "foo8", "REMOVE")))


        val columns = listOf("Expression", "Foo1", "Foo2", "Action")
        val model = object : DefaultTreeTableModel(root, columns) {
            override fun getColumnClass(column: Int): Class<*> {
                return when (column) {
                    0 -> TreeTableNode::class.java
                    else -> String::class.java
                }
            }
        }

        val treeTable = JXTreeTable(model).apply {
            expandAll()
            treeCellRenderer = CheckBoxTreeCellRenderer()
            rowHeight = 22
        }

        val scrollPane = JScrollPane(treeTable).apply {
            preferredSize = java.awt.Dimension(Integer.MAX_VALUE, 200)
            minimumSize = java.awt.Dimension(Integer.MAX_VALUE, 150)
        }

        return JPanel(BorderLayout()).apply {
            add(scrollPane, BorderLayout.CENTER)
        }
    }

    override fun panelEnd(): JPanel {
        return JPanel()
    }
}