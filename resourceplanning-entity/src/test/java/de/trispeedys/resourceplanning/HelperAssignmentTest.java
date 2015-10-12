package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.SpeedyTestUtil;
import de.trispeedys.resourceplanning.entity.util.DataModelUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.HelperService;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.test.TestDataProvider;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class HelperAssignmentTest
{
    public static final String TEST_MAIL_ADDRESS = "testhelper1.trispeedys@gmail.com";

    private static final String TEST_MAIL_PASSWORD = "trispeedys1234";

    /**
     * Denselben Helfer in der selben Veranstaltung auf zwei Positionen best�tigen. Das ist OK
     * m�glich.
     * 
     * @throws ResourcePlanningException
     */
    @Test
    public void testDuplicateAssignment() throws ResourcePlanningException
    {
        HibernateUtil.clearAll();

        Event event = EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2015).persist();

        Domain defaultDomain = SpeedyTestUtil.buildDefaultDomain(1);
        Position position1 = EntityFactory.buildPosition("Radverpflegung", 12, defaultDomain, false).persist();
        Position position2 = EntityFactory.buildPosition("Laufverpflegung", 16, defaultDomain, false).persist();

        // relate positions to event
        DataModelUtil.relatePositionsToEvent(event, position1, position2);

        Helper helper =
                EntityFactory.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 13, 2, 1976)
                        .persist();

        EntityFactory.buildHelperAssignment(helper, event, position2).persist();

        // confirm helper for another position of the same event
        AssignmentService.assignHelper(helper, event, position1);
    }

    /**
     * Einen Helfer f�r eine Position best�tigen, f�r die er zu jung ist, aber zusammen mit einem Erziehungsberechtigten
     * eingesetzt wird.
     */
    // TODO fix test
    // @Test
    public void testAssignmentUnderAgeAuthorityOverride()
    {
        HibernateUtil.clearAll();

        Event event = EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2016).persist();

        // Helfer ist zum Datum der Veranstaltung erst 15
        Helper helper =
                EntityFactory.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000)
                        .persist();

        // Position erfordert Mindest-Alter 16 Jahre
        Position position =
                EntityFactory.buildPosition("Laufverpflegung", 16, SpeedyTestUtil.buildDefaultDomain(1), true).persist();

        // Muss zu Ausnahme f�hren
        AssignmentService.assignHelper(helper, event, position);
    }

    /**
     * Einen Helfer f�r eine Position best�tigen, f�r die er zu jung ist
     * 
     * @throws ResourcePlanningException
     */
    @Test(expected = ResourcePlanningException.class)
    public void testAssignmentUnderAge() throws ResourcePlanningException
    {
        HibernateUtil.clearAll();

        Event event = EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2016).persist();

        // Helfer ist zum Datum der Veranstaltung erst 15
        Helper helper =
                EntityFactory.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000)
                        .persist();

        // Position erfordert Mindest-Alter 16 Jahre
        Position position =
                EntityFactory.buildPosition("Laufverpflegung", 16, SpeedyTestUtil.buildDefaultDomain(1), false)
                        .persist();

        // Muss zu Ausnahme f�hren
        AssignmentService.assignHelper(helper, event, position);
    }

    @Test
    public void testGetLastConfirmedHelperAssignment()
    {
        HibernateUtil.clearAll();

        // create some events
        Event evt2012 = EntityFactory.buildEvent("TRI-2012", "TRI-2012", 21, 6, 2012).persist();
        Event evt2014 = EntityFactory.buildEvent("TRI-2012", "TRI-2014", 21, 6, 2014).persist();

        // helper was confirmed for a position in 2012, but only proposed for one in 2014...
        Helper helper =
                EntityFactory.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000)
                        .persist();
        Position position =
                EntityFactory.buildPosition("Laufverpflegung", 16, SpeedyTestUtil.buildDefaultDomain(1), false)
                        .persist();

        // relate position to both events
        DataModelUtil.relateEventsToPosition(position, evt2012, evt2014);

        EntityFactory.buildHelperAssignment(helper, evt2012, position).persist();

        // last confirmed assignment should be in 2012
        HelperAssignment lastConfirmedAssignment = HelperService.getLastConfirmedAssignmentForHelper(helper);

        Calendar cal = Calendar.getInstance();
        cal.setTime(lastConfirmedAssignment.getEvent().getEventDate());
        assertEquals(2012, cal.get(Calendar.YEAR));
    }

    @Test
    public void testNoConfirmedHelperAssignment()
    {
        HibernateUtil.clearAll();

        // create some events
        Event event2012 = EntityFactory.buildEvent("TRI-2012", "TRI-2012", 21, 6, 2012).persist();
        Event event2014 = EntityFactory.buildEvent("TRI-2014", "TRI-2014", 21, 6, 2014).persist();

        // helper was confirmed for a position in 2012, but only proposed for one in 2014...
        Helper helper =
                EntityFactory.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000)
                        .persist();
        Position position =
                EntityFactory.buildPosition("Laufverpflegung", 16, SpeedyTestUtil.buildDefaultDomain(1), false)
                        .persist();

        // assign position to event
        DataModelUtil.relatePositionsToEvent(event2012, position);
        DataModelUtil.relatePositionsToEvent(event2014, position);

        // last confirmed assignment shuold be in 2012
        HelperAssignment lastConfirmedAssignment = HelperService.getLastConfirmedAssignmentForHelper(helper);

        assertEquals(null, lastConfirmedAssignment);
    }

    /**
     * Checks the availability of a position which the helper was assigned to before
     */
    @Test
    public void testPosAvailableForFollowingAssignment()
    {
        // clear database
        HibernateUtil.clearAll();

        // create a position
        Position position =
                EntityFactory.buildPosition("Laufverpflegung", 16, SpeedyTestUtil.buildDefaultDomain(1), false)
                        .persist();

        // helper was assigned pos 'Laufverpflegung' in 2015...
        Helper helperToReassign =
                EntityFactory.buildHelper("Stefan", "Schulz", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 2000)
                        .persist();
        Event event2015 = EntityFactory.buildEvent("TRI-2015", "TRI-2015", 21, 6, 2015).persist();

        // assign position to event
        DataModelUtil.relatePositionsToEvent(event2015, position);

        EntityFactory.buildHelperAssignment(helperToReassign, event2015, position).persist();

        // assign that position to another helper in 2016...
        Helper blockingHelper =
                EntityFactory.buildHelper("Klaus", "M�ller", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 1980)
                        .persist();
        Event event2016 = EntityFactory.buildEvent("TRI-2016", "TRI-2016", 21, 6, 2016).persist();

        // assign position to event
        DataModelUtil.relatePositionsToEvent(event2016, position);

        EntityFactory.buildHelperAssignment(blockingHelper, event2016, position).persist();

        // 'helperToReassign' can not be reassigned in 2016 as the position is assigned to 'blockingHelper'...
        assertFalse(PositionService.isPositionAvailable(event2016, HelperService.getLastConfirmedAssignmentForHelper(helperToReassign).getPosition()));
    }

    /**
     * Assign a helper to a position in an event WITH the position being part of that event
     */
    @Test
    public void testValidAssignment()
    {
        // clear db
        HibernateUtil.clearAll();
        // event
        Event event = EntityFactory.buildEvent("TRI-2016", "TRI-2016", 21, 6, 2016).persist();
        // helper
        Helper helper =
                EntityFactory.buildHelper("Klaus", "M�ller", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 1980)
                        .persist();
        // position
        Position position = EntityFactory.buildPosition("A", 10, SpeedyTestUtil.buildDefaultDomain(1), false).persist();
        // assign position to event
        EntityFactory.buildEventPosition(event, position).persist();
        // commit helper
        EntityFactory.buildHelperAssignment(helper, event, position);
    }

    /**
     * Assign a helper to a position in an event WITHOUT the position being part of that event
     */
    @Test(expected = ResourcePlanningException.class)
    public void testInvalidAssignment()
    {
        // clear db
        HibernateUtil.clearAll();
        // event
        Event event = EntityFactory.buildEvent("TRI-2016", "TRI-2016", 21, 6, 2016).persist();
        // helper
        Helper helper =
                EntityFactory.buildHelper("Klaus", "M�ller", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 1980)
                        .persist();
        // position
        Position position = EntityFactory.buildPosition("A", 10, SpeedyTestUtil.buildDefaultDomain(1), false).persist();
        
        // commit helper (position is not present in the event) --> must throw exception
        EntityFactory.buildHelperAssignment(helper, event, position);
    }

    /**
     * Tests querying {@link Position} in an event which are not already assigned to a {@link Helper}.
     */
    @Test
    public void testAvailablePositionsForEvent()
    {
        // clear db
        HibernateUtil.clearAll();

        // event
        Event event = TestDataProvider.createSimpleUnassignedEvent("TRI-2016", "TRI-2016", 21, 6, 2016);

        // helpers
        Helper helper1 =
                EntityFactory.buildHelper("Klaus", "M�ller", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 1980)
                        .persist();
        Helper helper2 =
                EntityFactory.buildHelper("Klaus", "M�ller", TEST_MAIL_ADDRESS, HelperState.ACTIVE, 23, 6, 1980)
                        .persist();

        // we have 5 positions...
        List<Position> positions = DatasourceRegistry.getDatasource(Position.class).findAll(Position.class);

        // ...and assign 2 of them...
        EntityFactory.buildHelperAssignment(helper1, event, positions.get(0)).persist();
        EntityFactory.buildHelperAssignment(helper2, event, positions.get(1)).persist();

        // ..and we expect 3 of them to be unassigned!!
        assertEquals(3, PositionService.findUnassignedPositionsInEvent(event).size());
    }
}