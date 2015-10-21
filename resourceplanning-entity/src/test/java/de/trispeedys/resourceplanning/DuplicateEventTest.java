package de.trispeedys.resourceplanning;

import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;

public class DuplicateEventTest
{
    @Test
    public void testEventAsTree()
    {
        HibernateUtil.clearAll();
        TestDataGenerator.createRealLifeEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.PLANNED);
        Event event = (Event) Datasources.getDatasource(Event.class).findAll().get(0);
        System.out.println(SpeedyRoutines.eventAsTree(event));
        List<AbstractDbObject> flatList = SpeedyRoutines.flattenedEventTree(event);
        SpeedyRoutines.debugEvent(event);
    }
    
    @Test
    public void testEventOutline()
    {
        HibernateUtil.clearAll();
        TestDataGenerator.createRealLifeEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.PLANNED);
        Event event = (Event) Datasources.getDatasource(Event.class).findAll().get(0);
        System.out.println(SpeedyRoutines.eventOutline(event));
    }
}