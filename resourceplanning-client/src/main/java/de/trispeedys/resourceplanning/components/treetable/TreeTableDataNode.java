package de.trispeedys.resourceplanning.components.treetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TreeTableDataNode
{
    private String name;

    private String capital;

    private Date declared;

    private Integer area;

    private List<TreeTableDataNode> children;

    public TreeTableDataNode(String name, String capital, Date declared, Integer area, List<TreeTableDataNode> children)
    {
        this.name = name;
        this.capital = capital;
        this.declared = declared;
        this.area = area;
        this.children = children;

        if (this.children == null)
        {
            this.children = Collections.emptyList();
        }
    }

    public String getName()
    {
        return name;
    }

    public String getCapital()
    {
        return capital;
    }

    public Date getDeclared()
    {
        return declared;
    }

    public Integer getArea()
    {
        return area;
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
        return name;
    }
}