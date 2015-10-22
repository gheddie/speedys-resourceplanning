package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.DomainRepository;
import de.trispeedys.resourceplanning.repository.EventPositionRepository;
import de.trispeedys.resourceplanning.repository.RepositoryProvider;
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

    /**
     * [E][D1][P0][P1][D2][P2][P137][P232][P398][D17][P38][P39][D92][P93][P94] + [D17].[P666] =
     * [E][D1][P0][P1][D2][P2][P137][P232][P398][D17][P38][P39][P666][D92][P93][P94]
     */
    @Test
    public void testDuplicateEventWithAddedPositions()
    {
        // clear db
        HibernateUtil.clearAll();

        // real life event for 2015
        Event event =
                TestDataGenerator.createRealLifeEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                        EventState.FINISHED);

        // add a position (with number 666) to a domain [D17] in that event
        Domain domain = RepositoryProvider.getRepository(DomainRepository.class).findDomainByNumber(17);
        Position pos = EntityFactory.buildPosition("", 99, domain, false, 666).persist();
        SpeedyRoutines.relatePositionsToEvent(event, pos);

        Event loadedEvent = (Event) Datasources.getDatasource(Event.class).findAll().get(0);
        assertEquals("[E][D1][P0][P1][D2][P2][P137][P232][P398][D17][P38][P39][P666][D92][P93][P94]",
                SpeedyRoutines.eventOutline(loadedEvent));
    }

    /**
     * [E][D1][P0][P1][D2][P2][P137][P232][P398][D17][P38][P39][D92][P93][P94] - [D2].[P137] =
     * [E][D1][P0][P1][D2][P2][P232][P398][D17][P38][P39][D92][P93][P94]
     */
    @Test
    public void testDuplicateEventWithRemovedPositions()
    {
        // clear db
        HibernateUtil.clearAll();
        
        // real life event for 2015
        Event event =
                TestDataGenerator.createRealLifeEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                        EventState.FINISHED);
        
        // do not remove position (leave it for later use), just remove the assignment to event (EventPosition)
        EventPosition eventPosition = RepositoryProvider.getRepository(EventPositionRepository.class).findByEventAndPositionNumber(event, 137);
        Datasources.getDatasource(EventPosition.class).delete(eventPosition);

        Event loadedEvent = (Event) Datasources.getDatasource(Event.class).findAll().get(0);
        assertEquals("[E][D1][P0][P1][D2][P2][P232][P398][D17][P38][P39][D92][P93][P94]",
                SpeedyRoutines.eventOutline(loadedEvent));
    }
}