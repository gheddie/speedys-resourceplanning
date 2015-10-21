package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.service.EventService;
import de.trispeedys.resourceplanning.test.TestDataGenerator;

public class DuplicateEventTest
{
    @Test
    public void testDebug()
    {
        HibernateUtil.clearAll();
        TestDataGenerator.createRealLifeEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015, EventState.PLANNED);
        System.out.println(EventService.debugEvent((Event) Datasources.getDatasource(Event.class).findAll().get(0)));
    }
}