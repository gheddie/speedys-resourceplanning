package de.trispeedys.resourceplanning.util;

import java.util.ArrayList;
import java.util.List;

public class EntityTreeNode<T>
{
    private Object payLoad;
    
    private List<Object> children;

    public EntityTreeNode(Object payLoad)
    {
        super();
        this.payLoad = payLoad;
    }
    
    public void acceptChild(Object child)
    {
        if (children == null)
        {
            children = new ArrayList<Object>();
        }
        children.add(child);
    }

    public Object getPayLoad()
    {
        return payLoad;
    }
    
    public List<Object> getChildren()
    {
        return children;
    }
    
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "[payLoad : "+payLoad+", "+children.size()+" children]";
    }
}