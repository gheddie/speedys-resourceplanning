package de.trispeedys.resourceplanning.util;

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
        Helper helper = ((AssignmentContainer) getPayLoad()).getHelper();
        String helperString = "";
        if (helper != null)
        {
            helperString = helper.toString();
        }
        else
        {
            helperString = "[--N/A--]";
        }
        return ((AssignmentContainer) getPayLoad()).getPosition() + " ["+helperString+"]";
    }
    
    public HierarchicalEventItemType getItemType()
    {
        return HierarchicalEventItemType.POSITION;
    }
}