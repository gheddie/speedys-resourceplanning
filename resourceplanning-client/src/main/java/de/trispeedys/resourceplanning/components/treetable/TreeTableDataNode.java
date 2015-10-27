package de.trispeedys.resourceplanning.components.treetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TreeTableDataNode
{
    private String description;
    
    private String assignment;

    private List<TreeTableDataNode> children;

    public TreeTableDataNode(String description, String assignment, List<TreeTableDataNode> children)
    {
        this.description = description;
        this.assignment = assignment;
        this.children = children;

        if (this.children == null)
        {
            this.children = Collections.emptyList();
        }
    }

    public String getDescription()
    {
        return description;
    }
    
    public String getAssignment()
    {
        return assignment;
    }

    public List<TreeTableDataNode> getChildren()
    {
        return children;
    }
    
    public void setChildren(List<TreeTableDataNode> children)
    {
        this.children = children;
    }
    
    public void addChild(TreeTableDataNode child)
    {
        if (children == null)
        {
            children = new ArrayList<TreeTableDataNode>();
        }
        children.add(child);
    }

    /**
     * Knotentext vom JTree.
     */
    public String toString()
    {
        return description;
    }
}