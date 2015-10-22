package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;

public class DuplicateEventTest
{
    @Test
    public void testDuplicateEventWithoutModifications()
    {
        HibernateUtil.clearAll();

        Event event2015 =
                TestDataGenerator.createRealLifeEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                        EventState.FINISHED);
        SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        List<Event> events = Datasources.getDatasource(Event.class).findAll();
        assertTrue(SpeedyRoutines.eventOutline(events.get(0)).equals(
                SpeedyRoutines.eventOutline(events.get(1))));
    }

    //@Test
    public void testDuplicateEventWithAddedPositions()
    {
        HibernateUtil.clearAll();

        // real life event for 2015
        Event realLifeEvent = TestDataGenerator.createRealLifeEvent(
                "Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.FINISHED);
        
        // add a position to a domain in that event
        Position posNew = EntityFactory.buildPosition("Ansage Zieleinlauf", 12, null, false, 0).persist();
        
        List<Event> events = Datasources.getDatasource(Event.class).findAll();
        System.out.println(SpeedyRoutines.eventOutline(events.get(0)));
    }
}