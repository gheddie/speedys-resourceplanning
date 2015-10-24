package de.trispeedys.resourceplanning.util;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;

public class DomainTreeNode<T> extends EntityTreeNode<Domain>
{
    public DomainTreeNode(Object payLoad)
    {
        super(payLoad);
    }

    public int getHierarchyLevel()
    {
        return HierarchicalEventItem.LEVEL_DOMAIN;
    }

    public String infoString()
    {
        return getPayLoad().toString();
    }
}