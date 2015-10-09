package de.trispeedys.resourceplanning.test;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.DataModelUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class TestDataProvider
{
    private static final String MAIL_ADDRESS = "testhelper1.trispeedys@gmail.com";

    /**
     * creates an {@link Event} like {@link TestDataProvider#createMinimalEvent(String, String, int, int, int)}, but
     * without assignments of positions to helpers.
     * 
     * @return
     */
    public static Event createSimpleUnassignedEvent(String description, String eventKey, int day, int month, int year)
    {
        // build event
        Event myLittleEvent = EntityFactory.buildEvent(description, eventKey, day, month, year).persist();

        // create helpers
        Helper helper1 =
                EntityFactory.buildHelper("H1_First", "H1_Last", MAIL_ADDRESS, HelperState.ACTIVE, 1, 1, 1980)
                        .persist();
        Helper helper3 =
                EntityFactory.buildHelper("H3_First", "H3_Last", MAIL_ADDRESS, HelperState.ACTIVE, 3, 1, 1980)
                        .persist();

        // build domains
        Domain domain1 = EntityFactory.buildDomain("D1", 1).persist();
        Domain domain2 = EntityFactory.buildDomain("D2", 1).persist();
        // build positions
        Position pos1 = EntityFactory.buildPosition("P1", 12, domain1, false).persist();
        Position pos2 = EntityFactory.buildPosition("P2", 12, domain1, false).persist();
        Position pos3 = EntityFactory.buildPosition("P3", 12, domain2, false).persist();
        Position pos4 = EntityFactory.buildPosition("P4", 12, domain2, false).persist();
        Position pos5 = EntityFactory.buildPosition("P5", 12, domain2, false).persist();
        // assign positions to event
        DataModelUtil.relatePositionsToEvent(myLittleEvent, pos1, pos2, pos3, pos4, pos5);

        return myLittleEvent;
    }

    /**
     * creates a litte test event ('My little event') with 2 domains D1 (positios: P1, P2) and D2 (positios: P3, P4,
     * P5). Every position is assigned to helper with a similar name (H1<->P1 etc. pp.). Every domain is lead by by the
     * first helper (H1 leads D1, H3 leads D2).
     * 
     * @param k
     * @param j
     * @param i
     * @param string2
     * @param string
     * @return
     */
    public static Event createSimpleEvent(String description, String eventKey, int day, int month, int year)
    {
        // build event
        Event myLittleEvent = EntityFactory.buildEvent(description, eventKey, day, month, year).persist();
        // create helpers
        Helper helper1 =
                EntityFactory.buildHelper("H1_First", "H1_Last", MAIL_ADDRESS, HelperState.ACTIVE, 1, 2, 1980)
                        .persist();
        Helper helper2 =
                EntityFactory.buildHelper("H2_First", "H2_Last", MAIL_ADDRESS, HelperState.ACTIVE, 2, 2, 1980)
                        .persist();
        Helper helper3 =
                EntityFactory.buildHelper("H3_First", "H3_Last", MAIL_ADDRESS, HelperState.ACTIVE, 3, 2, 1980)
                        .persist();
        Helper helper4 =
                EntityFactory.buildHelper("H4_First", "H4_Last", MAIL_ADDRESS, HelperState.ACTIVE, 4, 2, 1980)
                        .persist();
        Helper helper5 =
                EntityFactory.buildHelper("H5_First", "H5_Last", MAIL_ADDRESS, HelperState.ACTIVE, 5, 2, 1980)
                        .persist();
        // build domains
        Domain domain1 = EntityFactory.buildDomain("D1", 1).persist();
        Domain domain2 = EntityFactory.buildDomain("D2", 1).persist();
        // build positions
        Position pos1 = EntityFactory.buildPosition("P1", 12, domain1, false).persist();
        Position pos2 = EntityFactory.buildPosition("P2", 12, domain1, false).persist();
        Position pos3 = EntityFactory.buildPosition("P3", 12, domain2, false).persist();
        Position pos4 = EntityFactory.buildPosition("P4", 12, domain2, false).persist();
        Position pos5 = EntityFactory.buildPosition("P5", 12, domain2, false).persist();
        // assign positions to event
        DataModelUtil.relatePositionsToEvent(myLittleEvent, pos1, pos2, pos3, pos4, pos5);
        // assign helpers to positions
        DataModelUtil.assignHelperToPositions(helper1, myLittleEvent, pos1);
        DataModelUtil.assignHelperToPositions(helper2, myLittleEvent, pos2);
        DataModelUtil.assignHelperToPositions(helper3, myLittleEvent, pos3);
        DataModelUtil.assignHelperToPositions(helper4, myLittleEvent, pos4);
        DataModelUtil.assignHelperToPositions(helper5, myLittleEvent, pos5);

        return myLittleEvent;
    }

    /**
     * creates the minimal event : one domain, one position and one helper assigned to it.
     * 
     * @param description
     * @param eventKey
     * @param day
     * @param month
     * @param year
     * @return
     */
    public static Event createMinimalEvent(String description, String eventKey, int day, int month, int year)
    {
        // build event
        Event myMinimalEvent = EntityFactory.buildEvent(description, eventKey, day, month, year).persist();
        // create helper
        Helper helper =
                EntityFactory.buildHelper("H1_First", "H1_Last", MAIL_ADDRESS, HelperState.ACTIVE, 1, 1, 1980)
                        .persist();
        // build domain
        Domain domain = EntityFactory.buildDomain("D1", 1).persist();
        // build position
        Position pos = EntityFactory.buildPosition("P1", 12, domain, false).persist();
        // assign position to event
        DataModelUtil.relatePositionsToEvent(myMinimalEvent, pos);
        // assign helper to position
        DataModelUtil.assignHelperToPositions(helper, myMinimalEvent, pos);
        return myMinimalEvent;
    }

    // ---

    public static void main(String[] args)
    {
        HibernateUtil.clearAll();
        // ---
// Long event1 = createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId();
// Long event2 = DatabaseRoutines.duplicateEvent(event1, "Triathlon 2016", "TRI-2016", 21, 6, 2016).getId();
// System.out.println(DebugEvent.debugEvent(event1));
// System.out.println(DebugEvent.debugEvent(event2));
        // ---
// Long eventId = createMinimalEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId();
// System.out.println(DebugEvent.debugEvent(eventId));
        // ---
        Long evtId = createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId();
        DatabaseRoutines.duplicateEvent(evtId, "Triathlon 2016", "TRI-2016", 21, 6, 2016);
    }
}