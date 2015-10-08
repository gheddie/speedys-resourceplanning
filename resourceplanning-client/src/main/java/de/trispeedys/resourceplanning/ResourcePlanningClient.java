package de.trispeedys.resourceplanning;

import de.trispeedys.resourceplanning.webservice.ResourceInfoService;


/**
 * wsimport -keep -verbose http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/ResourceInfoWs?wsdl
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
        
        new ResourceInfoService().getResourceInfoPort().startOneProcesses();
        
//        new ResourceInfoService().getResourceInfoPort().sendAllMessages();
    }
}