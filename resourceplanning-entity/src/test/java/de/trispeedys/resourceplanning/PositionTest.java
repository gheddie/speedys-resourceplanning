package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.EventOccurence;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.builder.EntityBuilder;
import de.trispeedys.resourceplanning.service.PositionService;

public class PositionTest
{
    @Test
    public void testEventPositions()
    {
        HibernateUtil.clearAll();
        
        //some occurences
        EventOccurence eventOccurence1 = EntityBuilder.buildEventOccurence("DM AK 2014", "DM-AK-2014", 21, 6, 2014).persist();
        EventOccurence eventOccurence2 = EntityBuilder.buildEventOccurence("DM AK 2015", "DM-AK-2015", 21, 6, 2015).persist();
        
        //some positions
        Position position1 = EntityBuilder.buildPosition("Radverpflegung", 12).persist();
        Position position2 = EntityBuilder.buildPosition("Zielverpflegung", 12).persist();
        Position position3 = EntityBuilder.buildPosition("Irgendwas kontrollieren", 12).persist();
        Position position4 = EntityBuilder.buildPosition("Gut aussehen", 12).persist();
        
        //some links between occurence 1 and positions
        EntityBuilder.buildEventPosition(eventOccurence1, position1).persist();
        EntityBuilder.buildEventPosition(eventOccurence1, position3).persist();
        EntityBuilder.buildEventPosition(eventOccurence1, position4).persist();
        
        //some links between occurence 2 and positions
        EntityBuilder.buildEventPosition(eventOccurence2, position1).persist();
        EntityBuilder.buildEventPosition(eventOccurence2, position3).persist();
        
        //occurence 1 should have 3 positions
        assertEquals(3, PositionService.findPositionsInEventOccurence(eventOccurence1).size());
        
        //occurence 2 should have 2 positions
        assertEquals(2, PositionService.findPositionsInEventOccurence(eventOccurence2).size());
    }
}