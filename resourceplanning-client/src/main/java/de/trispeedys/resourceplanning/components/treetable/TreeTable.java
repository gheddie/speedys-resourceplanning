package de.trispeedys.resourceplanning.components.treetable;

import java.awt.Dimension;

import javax.swing.JTable;

public class TreeTable extends JTable
{

    private TreeTableCellRenderer tree;

    public TreeTable(AbstractTreeTableModel treeTableModel)
    {
        super();

        // JTree erstellen.
        tree = new TreeTableCellRenderer(this, treeTableModel);

        // Modell setzen.
        super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

        // Gleichzeitiges Selektieren fuer Tree und Table.
        TreeTableSelectionModel selectionModel = new TreeTableSelectionModel();
        tree.setSelectionModel(selectionModel); // For the tree
        setSelectionModel(selectionModel.getListSelectionModel()); // For the table

        // Renderer fuer den Tree.
        setDefaultRenderer(MyTreeTableModel.class, tree);
        // Editor fuer die TreeTable
        setDefaultEditor(MyTreeTableModel.class, new TreeTableCellEditor(tree, this));

        // Kein Grid anzeigen.
        setShowGrid(false);

        // Keine Abstaende.
        setIntercellSpacing(new Dimension(0, 0));

    }
}