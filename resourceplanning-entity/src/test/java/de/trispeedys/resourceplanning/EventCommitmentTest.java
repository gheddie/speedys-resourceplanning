package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Calendar;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.builder.EntityBuilder;
import de.trispeedys.resourceplanning.entity.misc.EventCommitmentState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.service.CommitmentService;
import de.trispeedys.resourceplanning.service.HelperService;

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
        
        Event event = EntityBuilder.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2015).persist();
        
        Position position1 = EntityBuilder.buildPosition("Radverpflegung", 12).persist();
        Position position2 = EntityBuilder.buildPosition("Laufverpflegung", 16).persist();
        
        Helper helper = EntityBuilder.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 13, 2, 1976).persist();
        
        EntityBuilder.buildEventCommitment(helper, event, position2, EventCommitmentState.CONFIRMED).persist();
        
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
        
        Event event = EntityBuilder.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2016).persist();
        
        //Helfer ist zum Datum der Veranstaltung erst 15 
        Helper helper = EntityBuilder.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000).persist();
        
        //Position erfordert Mindest-Alter 16 Jahre
        Position position = EntityBuilder.buildPosition("Laufverpflegung", 16).persist();
        
        //Muss zu Ausnahme führen
        CommitmentService.confirmHelper(helper, event, position);
    }
    
    @Test    
    public void testGetLastConfirmedCommitment()
    {
        HibernateUtil.clearAll();
        
        //create some events
        Event oc2012 = EntityBuilder.buildEvent("TRI-2012", "TRI-2012", 21, 6, 2012).persist();
        Event oc2014 = EntityBuilder.buildEvent("TRI-2012", "TRI-2014", 21, 6, 2014).persist();
        
        //helper was confirmed for a position in 2012, but only proposed for one in 2014...
        Helper helper = EntityBuilder.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000).persist();
        Position position = EntityBuilder.buildPosition("Laufverpflegung", 16).persist();
        EntityBuilder.buildEventCommitment(helper, oc2012, position, EventCommitmentState.CONFIRMED).persist();
        EntityBuilder.buildEventCommitment(helper, oc2014, position, EventCommitmentState.PROPOSED).persist();
        
        //last confirmed commitment shuold be in 2012
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
        Event oc2012 = EntityBuilder.buildEvent("TRI-2012", "TRI-2012", 21, 6, 2012).persist();
        Event oc2014 = EntityBuilder.buildEvent("TRI-2014", "TRI-2014", 21, 6, 2014).persist();
        
        //helper was confirmed for a position in 2012, but only proposed for one in 2014...
        Helper helper = EntityBuilder.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000).persist();
        Position position = EntityBuilder.buildPosition("Laufverpflegung", 16).persist();
        EntityBuilder.buildEventCommitment(helper, oc2012, position, EventCommitmentState.PROPOSED).persist();
        EntityBuilder.buildEventCommitment(helper, oc2014, position, EventCommitmentState.PROPOSED).persist();
        
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
        Position position = EntityBuilder.buildPosition("Laufverpflegung", 16).persist();
        
        //helper was assigned pos 'Laufverpflegung' in 2015...
        Helper helperToReassign = EntityBuilder.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000).persist();        
        Event oc2015 = EntityBuilder.buildEvent("TRI-2015", "TRI-2015", 21, 6, 2015).persist();
        EntityBuilder.buildEventCommitment(helperToReassign, oc2015, position, EventCommitmentState.CONFIRMED).persist();
        
        //assign that position to another helper in 2016...
        Helper blockingHelper = EntityBuilder.buildHelper("Klaus", "Müller", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 1980).persist();
        Event oc2016 = EntityBuilder.buildEvent("TRI-2016", "TRI-2016", 21, 6, 2016).persist();
        EntityBuilder.buildEventCommitment(blockingHelper, oc2016, position, EventCommitmentState.CONFIRMED).persist();
        
        //'helperToReassign' can not be reassigned in 2016 as the position is assigned to 'blockingHelper'...
        assertFalse(HelperService.isHelperReassignableToSamePosition(oc2016.getId(), helperToReassign.getId()));
    }
}