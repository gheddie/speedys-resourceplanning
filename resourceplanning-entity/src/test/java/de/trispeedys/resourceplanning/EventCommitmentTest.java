package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Calendar;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.DataModelUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.service.CommitmentService;
import de.trispeedys.resourceplanning.service.HelperService;
import de.trispeedys.resourceplanning.util.exception.DataModelException;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class EventCommitmentTest
{
    public static final String TEST_MAIL_ADDRESS = "testhelper1.trispeedys@gmail.com";
    
    private static final String TEST_MAIL_PASSWORD = "trispeedys1234";

	/**
     * Denselben Helfer in der selben Veranstaltung auf zwei Positionen bestätigen
     * 
     * @throws ResourcePlanningException
     */
    @Test(expected = ResourcePlanningException.class)
    public void testDuplicateCommitment() throws ResourcePlanningException
    {
        HibernateUtil.clearAll();
        
        Event event = EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2015).persist();
        
        Position position1 = EntityFactory.buildPosition("Radverpflegung", 12).persist();
        Position position2 = EntityFactory.buildPosition("Laufverpflegung", 16).persist();
        
        //relate positions to event
        DataModelUtil.relatePositionsToEvent(event, position1, position2);
        
        Helper helper = EntityFactory.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 13, 2, 1976).persist();
        
        EntityFactory.buildEventCommitment(helper, event, position2).persist();
        
        //confirm helper for another position of the same event
        CommitmentService.confirmHelper(helper, event, position1);
    }
    
    /**
     * Einen Helfer für eine Position bestätigen, für die er zu jung ist
     * @throws ResourcePlanningException 
     */
    @Test(expected = ResourcePlanningException.class)
    public void testCommitmentUnderAge() throws ResourcePlanningException
    {
        HibernateUtil.clearAll();
        
        Event event = EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2016).persist();
        
        //Helfer ist zum Datum der Veranstaltung erst 15 
        Helper helper = EntityFactory.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000).persist();
        
        //Position erfordert Mindest-Alter 16 Jahre
        Position position = EntityFactory.buildPosition("Laufverpflegung", 16).persist();
        
        //Muss zu Ausnahme führen
        CommitmentService.confirmHelper(helper, event, position);
    }
    
    @Test    
    public void testGetLastConfirmedCommitment()
    {
        HibernateUtil.clearAll();
        
        //create some events
        Event evt2012 = EntityFactory.buildEvent("TRI-2012", "TRI-2012", 21, 6, 2012).persist();
        Event evt2014 = EntityFactory.buildEvent("TRI-2012", "TRI-2014", 21, 6, 2014).persist();
        
        //helper was confirmed for a position in 2012, but only proposed for one in 2014...
        Helper helper = EntityFactory.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000).persist();
        Position position = EntityFactory.buildPosition("Laufverpflegung", 16).persist();
        
        //relate position to both events
        DataModelUtil.relateEventsToPosition(position, evt2012, evt2014);
        
        EntityFactory.buildEventCommitment(helper, evt2012, position).persist();
        
        //last confirmed commitment should be in 2012
        EventCommitment lastConfirmedAssignment = HelperService.getLastConfirmedAssignmentForHelper(helper.getId());
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastConfirmedAssignment.getEvent().getEventDate());
        assertEquals(2012, cal.get(Calendar.YEAR));
    }
    
    @Test    
    public void testNoConfirmedCommitment()
    {
        HibernateUtil.clearAll();
        
        //create some events
        Event event2012 = EntityFactory.buildEvent("TRI-2012", "TRI-2012", 21, 6, 2012).persist();
        Event event2014 = EntityFactory.buildEvent("TRI-2014", "TRI-2014", 21, 6, 2014).persist();
        
        //helper was confirmed for a position in 2012, but only proposed for one in 2014...
        Helper helper = EntityFactory.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000).persist();
        Position position = EntityFactory.buildPosition("Laufverpflegung", 16).persist();
        
        //assign position to event
        DataModelUtil.relatePositionsToEvent(event2012, position);
        DataModelUtil.relatePositionsToEvent(event2014, position);
                
        //last confirmed commitment shuold be in 2012
        EventCommitment lastConfirmedAssignment = HelperService.getLastConfirmedAssignmentForHelper(helper.getId());
        
        assertEquals(null, lastConfirmedAssignment);
    }    
    
    /**
     * Checks the availability of a position which the helper
     * was assigned to before 
     */
    @Test    
    public void testPosAvailableForFollowingAssignment()
    {
        //clear database
        HibernateUtil.clearAll();
        
        //create a position
        Position position = EntityFactory.buildPosition("Laufverpflegung", 16).persist();
        
        //helper was assigned pos 'Laufverpflegung' in 2015...
        Helper helperToReassign = EntityFactory.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000).persist();        
        Event event2015 = EntityFactory.buildEvent("TRI-2015", "TRI-2015", 21, 6, 2015).persist();
        
        //assign position to event
        DataModelUtil.relatePositionsToEvent(event2015, position);
        
        EntityFactory.buildEventCommitment(helperToReassign, event2015, position).persist();
        
        //assign that position to another helper in 2016...
        Helper blockingHelper = EntityFactory.buildHelper("Klaus", "Müller", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 1980).persist();
        Event event2016 = EntityFactory.buildEvent("TRI-2016", "TRI-2016", 21, 6, 2016).persist();
        
        //assign position to event
        DataModelUtil.relatePositionsToEvent(event2016, position);
        
        EntityFactory.buildEventCommitment(blockingHelper, event2016, position).persist();
        
        //'helperToReassign' can not be reassigned in 2016 as the position is assigned to 'blockingHelper'...
        assertFalse(HelperService.isHelperReassignableToSamePosition(event2016.getId(), helperToReassign.getId()));
    }
    
    /**
     * Assign a helper to a position in an event WITH the position being part of that event
     */
    @Test    
    public void testValidCommitment()
    {
        //clear db
        HibernateUtil.clearAll();
        //event
        Event event = EntityFactory.buildEvent("TRI-2016", "TRI-2016", 21, 6, 2016).persist();
        //helper
        Helper helper = EntityFactory.buildHelper("Klaus", "Müller", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 1980).persist();
        //position
        Position position = EntityFactory.buildPosition("A", 10).persist();
        //assign position to event
        EntityFactory.buildEventPosition(event, position).persist();
        //commit helper
        EntityFactory.buildEventCommitment(helper, event, position);
    }
    
    /**
     * Assign a helper to a position in an event WITHOUT the position being part of that event
     */
    @Test(expected = DataModelException.class)
    public void testInvalidCommitment()
    {
        //clear db
        HibernateUtil.clearAll();
        //event
        Event event = EntityFactory.buildEvent("TRI-2016", "TRI-2016", 21, 6, 2016).persist();
        //helper
        Helper helper = EntityFactory.buildHelper("Klaus", "Müller", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 1980).persist();
        //position
        Position position = EntityFactory.buildPosition("A", 10).persist();
        //commit helper (position is not present in the event)
        EntityFactory.buildEventCommitment(helper, event, position);
    }    
}