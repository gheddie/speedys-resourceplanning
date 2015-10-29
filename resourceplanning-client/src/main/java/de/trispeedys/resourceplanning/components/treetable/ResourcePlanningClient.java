package de.trispeedys.resourceplanning.components.treetable;

import de.trispeedys.resourceplanning.entity.misc.HierarchicalEventItem;
import de.trispeedys.resourceplanning.webservice.HierarchicalEventItemDTO;
import de.trispeedys.resourceplanning.webservice.ResourceInfoService;

/**
 * wsimport -keep -verbose http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/ResourceInfoWs?wsdl
 * 
 * wsimport -keep -verbose http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/TestDataProviderWs?wsdl
 * 
 * @author Stefan.Schulz
 *
 * http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/HelperCallbackReceiver.jsp?callbackResult=ASSIGNMENT_AS_BEFORE
 */
public class ResourcePlanningClient
{
    public static void main(String[] args)
    {        
//        new ResourceInfoService().getResourceInfoPort().startSomeProcesses();
        
//        new ResourceInfoService().getResourceInfoPort().startOneProcesses();
        
//        new ResourceInfoService().getResourceInfoPort().sendAllMessages();
        
//        new ResourceInfoService().getResourceInfoPort().startSomeProcessesWithNewHelpers();
        
//        new ResourceInfoService().getResourceInfoPort().processHelperCallback(arg0, arg1);
        
        for (HierarchicalEventItemDTO node : new ResourceInfoService().getResourceInfoPort().getEventNodes(new Long(5200), false).getItem())
        {
            switch (node.getHierarchyLevel())
            {
                case HierarchicalEventItem.LEVEL_EVENT:
                    System.out.println(" + " + node.getInfoString());
                    break;
                case HierarchicalEventItem.LEVEL_DOMAIN:
                    System.out.println("   + " + node.getInfoString());
                    break;
                case HierarchicalEventItem.LEVEL_POSITION:
                    System.out.println("      + " + node.getInfoString());
                    break;
            }
        }
    }
}