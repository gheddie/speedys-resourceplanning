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
    public static TreeTableDataNode createDataStructure()
    {
        TreeTableDataNode eventNode = null;
        TreeTableDataNode domainNode = null;
        List<TreeTableDataNode> domainNodes = null;
        List<TreeTableDataNode> positionNodes = null;
        
        for (HierarchicalEventItemDTO node : new ResourceInfoService().getResourceInfoPort().getNodes(new Long(6291)).getItem())
        {
            switch (node.getHierarchyLevel())
            {
                case HierarchicalEventItem.LEVEL_EVENT:
                    eventNode = new TreeTableDataNode(node.getInfoString(), node.getAssignmentString(), new Date(), 1, null);
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
                    domainNode = new TreeTableDataNode(node.getInfoString(), node.getAssignmentString(), new Date(), 1, null);
                    positionNodes = new ArrayList<TreeTableDataNode>();
                    break;
                case HierarchicalEventItem.LEVEL_POSITION:
                    positionNodes.add(new TreeTableDataNode(node.getInfoString(), node.getAssignmentString(), new Date(), 1, null));
                    break;
            }
        }
        
        eventNode.setChildren(domainNodes);
        return eventNode;
        
        //---
        
        /*
        List<TreeTableDataNode> children1 = new ArrayList<TreeTableDataNode>();
        children1.add(new TreeTableDataNode("N12", "C12", new Date(), Integer.valueOf(50), null));
        children1.add(new TreeTableDataNode("N13", "C13", new Date(), Integer.valueOf(60), null));

        List<TreeTableDataNode> children2 = new ArrayList<TreeTableDataNode>();
        children2.add(new TreeTableDataNode("N12", "C12", new Date(), Integer.valueOf(10), null));
        children2.add(new TreeTableDataNode("N14", "C14", new Date(), Integer.valueOf(30), null));
        children2.add(new TreeTableDataNode("N15", "C15", new Date(), Integer.valueOf(40), null));

        List<TreeTableDataNode> rootNodes = new ArrayList<TreeTableDataNode>();
        rootNodes.add(new TreeTableDataNode("N1", "C1", new Date(), Integer.valueOf(10), children2));
        rootNodes.add(new TreeTableDataNode("N2", "C2", new Date(), Integer.valueOf(10), children1));

        TreeTableDataNode root = new TreeTableDataNode("R1", "R1", new Date(), Integer.valueOf(10), rootNodes);

        return root;
        */
    }
}