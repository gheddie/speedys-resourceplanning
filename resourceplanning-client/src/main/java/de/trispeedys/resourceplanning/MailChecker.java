package de.trispeedys.resourceplanning;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.SpeedyTestUtil;
import de.trispeedys.resourceplanning.entity.util.DataModelUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.webservice.ResourceInfo;
import de.trispeedys.resourceplanning.webservice.ResourceInfoService;

public class MailChecker
{
    public static void main(String[] args)
    {
// HibernateUtil.clearAll();

// startProcess();

// sendMails();
        
        /**
         * select * from helper
         * select * from event
         * select * from message_queue
         */

        startLittleEventProcess();
    }

    private static void startLittleEventProcess()
    {
        Long helperId = new Long(3390);
        Long eventId = new Long(3389);
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        new ResourceInfoService().getResourceInfoPort().startHelperRequestProcess(helperId, eventId, businessKey);
        
        new ResourceInfoService().getResourceInfoPort().sendMessages();
    }

    private static void sendMails()
    {
        new ResourceInfoService().getResourceInfoPort().sendMessages();
    }

    private static void startProcess()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create position
        Position positionBikeEntry =
                EntityFactory.buildPosition("Radeinfahrt Helmkontrolle", 12, SpeedyTestUtil.buildDefaultDomain(), false)
                        .persist();
        // create events
        Event evt2014 = EntityFactory.buildEvent("Triathlon 2014", "TRI-2014", 21, 6, 2014).persist();
        Event evt2015 = EntityFactory.buildEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).persist();
        // create helper
        Helper createdHelper =
                EntityFactory.buildHelper("Stefan", "Schulz", "testhelper1.trispeedys@gmail.com", HelperState.ACTIVE,
                        1, 1, 1990).persist();
        Helper blockingHelper =
                EntityFactory.buildHelper("Blocking", "Helper", "b@l.com", HelperState.ACTIVE, 1, 1, 1990).persist();
        // assign position to event
        DataModelUtil.relateEventsToPosition(positionBikeEntry, evt2014, evt2015);
        // assign helper to position in 2014
        EntityFactory.buildEventCommitment(createdHelper, evt2014, positionBikeEntry).persist();
        // assign position to another helper in 2015
        EntityFactory.buildEventCommitment(blockingHelper, evt2015, positionBikeEntry).persist();
        // start request process for 2015...
        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(createdHelper.getId(), evt2015.getId());
        ResourceInfo resourceInfoService = new ResourceInfoService().getResourceInfoPort();
        resourceInfoService.startHelperRequestProcess(createdHelper.getId(), evt2015.getId(), businessKey);
    }
}