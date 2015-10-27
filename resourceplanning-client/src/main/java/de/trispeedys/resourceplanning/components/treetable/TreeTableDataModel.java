package de.trispeedys.resourceplanning.components.treetable;

import java.util.Date;

public class TreeTableDataModel extends AbstractTreeTableModel
{
    // Spalten Name.
    static protected String[] columnNames =
    {
            "Beschreibung", "Besetzung"
    };

    // Spalten Typen.
    static protected Class<?>[] columnTypes =
    {
            MyTreeTableModel.class, String.class, Date.class, Integer.class
    };

    public TreeTableDataModel(TreeTableDataNode rootNode)
    {
        super(rootNode);
        root = rootNode;
    }

    public Object getChild(Object parent, int index)
    {
        return ((TreeTableDataNode) parent).getChildren().get(index);
    }

    public int getChildCount(Object parent)
    {
        return ((TreeTableDataNode) parent).getChildren().size();
    }

    public int getColumnCount()
    {
        return columnNames.length;
    }

    public String getColumnName(int column)
    {
        return columnNames[column];
    }

    public Class<?> getColumnClass(int column)
    {
        return columnTypes[column];
    }

    public Object getValueAt(Object node, int column)
    {
        switch (column)
        {
            case 0:
                return ((TreeTableDataNode) node).getDescription();
            case 1:
                return ((TreeTableDataNode) node).getAssignment();
            default:
                break;
        }
        return null;
    }

    public boolean isCellEditable(Object node, int column)
    {
        return true; // Important to activate TreeExpandListener
    }

    public void setValueAt(Object aValue, Object node, int column)
    {
    }

}