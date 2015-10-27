package de.trispeedys.resourceplanning.util;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;

public class PositionTreeNode<T> extends EntityTreeNode<Position>
{
    public PositionTreeNode()
    {
        super();
    }
    
    public Object getHierarchicalItem()
    {
        return ((AssignmentContainer) getPayLoad()).getPosition();
    }
    
    public int getHierarchyLevel()
    {
        return HierarchicalEventItem.LEVEL_POSITION;
    }
    
    public String infoString()
    {
        return ((AssignmentContainer) getPayLoad()).getPosition().toString();
    }
    
    public HierarchicalEventItemType getItemType()
    {
        return HierarchicalEventItemType.POSITION;
    }
    
    public String itemKey()
    {
        Position eventItem = ((AssignmentContainer) getPayLoad()).getPosition();
        return eventItem.getDifferentiator();
    }
    
    public String getAssignmentString()
    {
        Helper helper = ((AssignmentContainer) getPayLoad()).getHelper();
        if (helper != null)
        {
            return helper.getLastName() + ", " + helper.getFirstName();
        }
        else
        {
            return "[--N/A--]";   
        }
    }
}