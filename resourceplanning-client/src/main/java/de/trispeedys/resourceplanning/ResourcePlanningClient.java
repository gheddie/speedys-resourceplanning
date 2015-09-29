package de.trispeedys.resourceplanning;

import de.trispeedys.resourceplanning.webservice.SomeTestService;
import de.trispeedys.resourceplanning.webservice.SomeTestServiceService;


/**
 * wsimport -keep -verbose http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/SomeTestServiceWs?wsdl
 * 
 * wsimport -keep -verbose http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/ResourceInfoWs?wsdl
 * 
 * @author Stefan.Schulz
 *
 */
public class ResourcePlanningClient
{
    public static void main(String[] args)
    {
        System.out.println("moo");
        
        SomeTestService testPort = new SomeTestServiceService().getSomeTestServicePort();        
        testPort.testStartProcess();
        
//        ResourceInfo resourceInfoService = new ResourceInfoService().getResourceInfoPort();
//        List<EventCommitmentDTO> commitments = resourceInfoService.queryCommitments("moo", "mee").getItem();
//        System.out.println(commitments.size() + " commitments queried.");
    }
}