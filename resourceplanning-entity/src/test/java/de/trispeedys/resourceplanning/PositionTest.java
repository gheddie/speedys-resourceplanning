package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.SpeedyTestUtil;
import de.trispeedys.resourceplanning.entity.util.DataModelUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.service.PositionService;

public class PositionTest
{
    @Test
    public void testEventPositions()
    {
        HibernateUtil.clearAll();

        // some events
        Event event1 = EntityFactory.buildEvent("DM AK 2014", "DM-AK-2014", 21, 6, 2014).persist();
        Event event2 = EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2015).persist();

        // some positions
        Domain defaultDomain = SpeedyTestUtil.buildDefaultDomain();
        Position position1 = EntityFactory.buildPosition("Radverpflegung", 12, defaultDomain, false).persist();
        Position position3 = EntityFactory.buildPosition("Irgendwas kontrollieren", 12, defaultDomain, false).persist();
        Position position4 = EntityFactory.buildPosition("Gut aussehen", 12, defaultDomain, false).persist();

        // some links between event 1 and positions
        EntityFactory.buildEventPosition(event1, position1).persist();
        EntityFactory.buildEventPosition(event1, position3).persist();
        EntityFactory.buildEventPosition(event1, position4).persist();

        // some links between event 2 and positions
        EntityFactory.buildEventPosition(event2, position1).persist();
        EntityFactory.buildEventPosition(event2, position3).persist();

        // event 1 should have 3 positions
        assertEquals(3, PositionService.findPositionsInEvent(event1).size());

        // event 2 should have 2 positions
        assertEquals(2, PositionService.findPositionsInEvent(event2).size());
    }

    @Test
    public void testPositionAssignment()
    {
        // clear db
        HibernateUtil.clearAll();
        // create helper
        Helper helper = EntityFactory.buildHelper("", "", "", HelperState.ACTIVE, 1, 1, 1980);
        // create events
        Event evt2013 = EntityFactory.buildEvent("TRI-2013", "TRI-2013", 21, 6, 2013).persist();
        Event evt2014 = EntityFactory.buildEvent("TRI-2014", "TRI-2014", 21, 6, 2014).persist();
        // create positions
        Domain defaultDomain = SpeedyTestUtil.buildDefaultDomain();
        Position posA = EntityFactory.buildPosition("A", 12, defaultDomain, false).persist();
        Position posB = EntityFactory.buildPosition("B", 12, defaultDomain, false).persist();
        Position posC = EntityFactory.buildPosition("C", 12, defaultDomain, false).persist();
        // event 2013 has positions (A,B,C)
        EntityFactory.buildEventPosition(evt2013, posA).persist();
        EntityFactory.buildEventPosition(evt2013, posB).persist();
        EntityFactory.buildEventPosition(evt2013, posC).persist();
        // event 2014 only has positions (A,B)
        EntityFactory.buildEventPosition(evt2014, posA).persist();
        EntityFactory.buildEventPosition(evt2014, posB).persist();

        // relate positions
        DataModelUtil.relatePositionsToEvent(evt2013, posA, posB, posC);
        DataModelUtil.relatePositionsToEvent(evt2014, posA, posB, posC);

        // assign positions
        EntityFactory.buildEventCommitment(helper, evt2013, posA);
        EntityFactory.buildEventCommitment(helper, evt2014, posC);
        // ...
        PositionService.isPositionAvailable(evt2014, posC);
    }
}