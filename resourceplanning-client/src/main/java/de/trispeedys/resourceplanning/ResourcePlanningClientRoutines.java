package de.trispeedys.resourceplanning;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.trispeedys.resourceplanning.components.treetable.TreeTableDataNode;
import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;
import de.trispeedys.resourceplanning.webservice.HierarchicalEventItemDTO;
import de.trispeedys.resourceplanning.webservice.ResourceInfoService;

public class ResourcePlanningClientRoutines
{
    public static TreeTableDataNode createDataStructure(Long eventId)
    {
        TreeTableDataNode eventNode = null;
        TreeTableDataNode domainNode = null;
        List<TreeTableDataNode> domainNodes = null;
        List<TreeTableDataNode> positionNodes = null;

        List<HierarchicalEventItemDTO> eventNodes = new ResourceInfoService().getResourceInfoPort()
                .getEventNodes(eventId)
                .getItem();
        for (HierarchicalEventItemDTO node : eventNodes)
        {
            switch (node.getHierarchyLevel())
            {
                case HierarchicalEventItem.LEVEL_EVENT:
                    eventNode =
                            new TreeTableDataNode(node.getInfoString(), node.getAssignmentString(),
                                    new Date(), 1, null);
                    domainNodes = new ArrayList<TreeTableDataNode>();
                    break;
                case HierarchicalEventItem.LEVEL_DOMAIN:
                    // finish up old node
                    if (domainNode != null)
                    {
                        domainNode.setChildren(positionNodes);
                        domainNodes.add(domainNode);
                    }
                    // create new node
                    domainNode =
                            new TreeTableDataNode(node.getInfoString(), node.getAssignmentString(),
                                    new Date(), 1, null);
                    positionNodes = new ArrayList<TreeTableDataNode>();
                    break;
                case HierarchicalEventItem.LEVEL_POSITION:
                    positionNodes.add(new TreeTableDataNode(node.getInfoString(), node.getAssignmentString(),
                            new Date(), 1, null));
                    break;
            }
        }
        
        // finally add last domain node (with pending children)
        if (domainNode != null)
        {
            domainNode.setChildren(positionNodes);
            domainNodes.add(domainNode);
        }

        eventNode.setChildren(domainNodes);
        return eventNode;
    }
}