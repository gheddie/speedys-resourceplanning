package de.trispeedys.resourceplanning.util;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;

public class EventTreeNode<T> extends EntityTreeNode<Event>
{
    public EventTreeNode(Object payLoad)
    {
        super(payLoad);
    }
    
    public int getHierarchyLevel()
    {
        return HierarchicalEventItem.LEVEL_EVENT;
    }
    
    public String infoString()
    {
        return getPayLoad().toString();
    }
}