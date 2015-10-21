package de.trispeedys.resourceplanning.util;

import java.util.HashSet;

public class EntityTreeNode
{
    private Object payLoad;
    
    private HashSet<Object> children;

    public EntityTreeNode(Object payLoad)
    {
        super();
        this.payLoad = payLoad;
    }
    
    public void acceptChild(Object child)
    {
        if (children == null)
        {
            children = new HashSet<Object>();
        }
        children.add(child);
    }

    public Object getPayLoad()
    {
        return payLoad;
    }
    
    public HashSet<Object> getChildren()
    {
        return children;
    }
    
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "[payLoad : "+payLoad+", "+children.size()+" children]";
    }
}