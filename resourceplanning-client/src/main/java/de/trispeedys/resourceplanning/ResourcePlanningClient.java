package de.trispeedys.resourceplanning;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.webservice.ResourceInfo;
import de.trispeedys.resourceplanning.webservice.ResourceInfoService;
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
//        SomeTestService testPort = new SomeTestServiceService().getSomeTestServicePort();        
//        testPort.testStartProcess();
        
        startProcessForFirstAssignment();
        
//        List<EventCommitmentDTO> commitments = resourceInfoService.queryCommitments("moo", "mee").getItem();
//        System.out.println(commitments.size() + " commitments queried.");
    }

    private static void startProcessForFirstAssignment()
    {
        ResourceInfo resourceInfoService = new ResourceInfoService().getResourceInfoPort();
        // clear all tables in db
        HibernateUtil.clearAll();
        // create event
        Event event = EntityFactory.buildEvent("Triathlon 2016", "TRI-2016", 21, 6, 2016).persist();
        // create helper
        Helper helper = EntityFactory.buildHelper("Stefan", "Schulz", "", HelperState.ACTIVE, 1, 1, 1990).persist();
        resourceInfoService.startHelperRequestProcess(helper.getId(), event.getId(), "moo");
    }
}