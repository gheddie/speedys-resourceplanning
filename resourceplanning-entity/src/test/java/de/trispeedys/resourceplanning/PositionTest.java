package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.builder.EntityBuilder;
import de.trispeedys.resourceplanning.entity.misc.EventCommitmentState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.service.PositionService;

public class PositionTest
{
    @Test
    public void testEventPositions()
    {
        HibernateUtil.clearAll();
        
        //some events
        Event event1 = EntityBuilder.buildEvent("DM AK 2014", "DM-AK-2014", 21, 6, 2014).persist();
        Event event2 = EntityBuilder.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2015).persist();
        
        //some positions
        Position position1 = EntityBuilder.buildPosition("Radverpflegung", 12).persist();
        Position position3 = EntityBuilder.buildPosition("Irgendwas kontrollieren", 12).persist();
        Position position4 = EntityBuilder.buildPosition("Gut aussehen", 12).persist();
        
        //some links between event 1 and positions
        EntityBuilder.buildEventPosition(event1, position1).persist();
        EntityBuilder.buildEventPosition(event1, position3).persist();
        EntityBuilder.buildEventPosition(event1, position4).persist();
        
        //some links between event 2 and positions
        EntityBuilder.buildEventPosition(event2, position1).persist();
        EntityBuilder.buildEventPosition(event2, position3).persist();
        
        //event 1 should have 3 positions
        assertEquals(3, PositionService.findPositionsInEvent(event1).size());
        
        //event 2 should have 2 positions
        assertEquals(2, PositionService.findPositionsInEvent(event2).size());
    }
    
    @Test
    public void testPositionAssignment()
    {
        //clear db
        HibernateUtil.clearAll();
        //create helper
        Helper helper = EntityBuilder.buildHelper("", "", "", HelperState.ACTIVE, 1, 1, 1980);
        //create events
        Event evt2013 = EntityBuilder.buildEvent("TRI-2013", "TRI-2013", 21, 6, 2013).persist();
        Event evt2014 = EntityBuilder.buildEvent("TRI-2014", "TRI-2014", 21, 6, 2014).persist();
        //create positions        
        Position posA = EntityBuilder.buildPosition("A", 12).persist();
        Position posB = EntityBuilder.buildPosition("B", 12).persist();
        Position posC = EntityBuilder.buildPosition("C", 12).persist();
        //event 2013 has positions (A,B,C)
        EntityBuilder.buildEventPosition(evt2013, posA).persist();
        EntityBuilder.buildEventPosition(evt2013, posB).persist();
        EntityBuilder.buildEventPosition(evt2013, posC).persist();
        //event 2014 only has positions (A,B)
        EntityBuilder.buildEventPosition(evt2014, posA).persist();
        EntityBuilder.buildEventPosition(evt2014, posB).persist();        
        //assign positions
        EntityBuilder.buildEventCommitment(helper, evt2013, posA, EventCommitmentState.CONFIRMED);
        EntityBuilder.buildEventCommitment(helper, evt2014, posC, EventCommitmentState.CONFIRMED);
        //...
        PositionService.isPositionAssigned(evt2014.getId(), posC);
    }
}