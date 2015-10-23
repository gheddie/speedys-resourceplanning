package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.DomainRepository;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class DuplicateEventTest
{
    @Test
    public void testDuplicateEventWithoutModifications()
    {
        HibernateUtil.clearAll();

        Event event2015 =
                TestDataGenerator.createRealLifeEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                        EventState.FINISHED, EventTemplate.TEMPLATE_TRI);
        SpeedyRoutines.duplicateEvent(event2015, "Triathlon 2016", "TRI-2016", 21, 6, 2016, null);
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
                        EventState.FINISHED, EventTemplate.TEMPLATE_TRI);

        // add a position (with number 666) to a domain [D17] in that event
        Domain domain = RepositoryProvider.getRepository(DomainRepository.class).findDomainByNumber(17);
        Position pos = EntityFactory.buildPosition("", 99, domain, false, 666).persist();
        SpeedyRoutines.relatePositionsToEvent(event, pos);

        Event loadedEvent = (Event) Datasources.getDatasource(Event.class).findAll().get(0);
        assertEquals("[E][D1][P0][P1][D2][P2][P137][P232][P398][D17][P38][P39][P666][D92][P93][P94]",
                SpeedyRoutines.eventOutline(loadedEvent));
    }

    /**
     * [E][D1][P0][P1][D2][P2][P137][P232][P398][D17][P38][P39][D92][P93][P94] - [D2].[P137] - [D2].[P398] - [D17].[P39]
     * = [E][D1][P0][P1][D2][P2][P232][D17][P38][D92][P93][P94]
     */
    @Test
    public void testDuplicateEventWithRemovedPositions()
    {
        // clear db
        HibernateUtil.clearAll();

        List<Integer> excludes = new ArrayList<Integer>();
        excludes.add(137);
        excludes.add(398);
        excludes.add(39);
        
        // real life event for 2015
        SpeedyRoutines.duplicateEvent(TestDataGenerator.createRealLifeEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                EventState.FINISHED, EventTemplate.TEMPLATE_TRI), "Triathlon 2016", "TRI-2016", 21, 6, 2016, excludes);

        List<Event> events =
                RepositoryProvider.getRepository(EventRepository.class).findEventByTemplateOrdered(
                        EventTemplate.TEMPLATE_TRI);

        Event loadedEvent2016 = events.get(1);
        
        assertEquals("[E][D1][P0][P1][D2][P2][P232][D17][P38][D92][P93][P94]",
                SpeedyRoutines.eventOutline(loadedEvent2016));
    }

    /**
     * Trying to exclude a positon on using {@link SpeedyRoutines#duplicateEvent(Event, String, String, int, int, int, List)} which is not there
     * in the actual event --> the must not be any db changes.
     */
    @Test(expected = ResourcePlanningException.class)
    public void testDuplicationExcludeFault()
    {
        // clear db
        HibernateUtil.clearAll();
        
        // this position can not be excluded (this is a fault) !!
        List<Integer> excludes = new ArrayList<Integer>();
        excludes.add(9876);
        
        // real life event for 2015
        Event realLifeEvent = TestDataGenerator.createRealLifeEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                EventState.FINISHED, EventTemplate.TEMPLATE_TRI);
        
        SpeedyRoutines.duplicateEvent(realLifeEvent, "Triathlon 2016", "TRI-2016", 21, 6, 2016, excludes);
    }
    
    @Test
    public void testSuccessfulEventDuplication()
    {
        // clear db
        HibernateUtil.clearAll();
        
        // an event with 5 positions...
        Event minimalEvent = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015,
                EventState.FINISHED, EventTemplate.TEMPLATE_TRI);
        
        SpeedyRoutines.duplicateEvent(minimalEvent, "Triathlon 2016", "TRI-2016", 21, 6, 2016, null);
        
        // checks (event + event pos count must be doubled, pos count must remain the same)
        assertEquals(2, Datasources.getDatasource(Event.class).findAll().size());
        assertEquals(10, Datasources.getDatasource(EventPosition.class).findAll().size());
        assertEquals(5, Datasources.getDatasource(Position.class).findAll().size());
    }
}