package de.trispeedys.resourceplanning.components;

import javax.swing.JTable;

public class ResourcePlanningTable extends JTable
{
    private static final long serialVersionUID = -5816470393008211811L;

    public boolean isCellEditable(int row, int column)
    {
        return false;
    }
}