package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.SpeedyTestUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;

public class PositionTest
{
    @Test
    public void testEventPositions()
    {
        HibernateUtil.clearAll();
        
        PositionRepository positionRepository = RepositoryProvider.getRepository(PositionRepository.class);
        
        EventTemplate template = EntityFactory.buildEventTemplate("123").saveOrUpdate();

        // some events
        Event event1 = EntityFactory.buildEvent("DM AK 2014", "DM-AK-2014", 21, 6, 2014, EventState.FINISHED, template).saveOrUpdate();
        Event event2 = EntityFactory.buildEvent("DM AK 2015", "DM-AK-2015", 21, 6, 2015, EventState.PLANNED, template).saveOrUpdate();

        // some positions
        Domain defaultDomain = SpeedyTestUtil.buildDefaultDomain(1);
        Position position1 = EntityFactory.buildPosition("Radverpflegung", 12, defaultDomain, 0, true).saveOrUpdate();
        Position position3 = EntityFactory.buildPosition("Irgendwas kontrollieren", 12, defaultDomain, 1, true).saveOrUpdate();
        Position position4 = EntityFactory.buildPosition("Gut aussehen", 12, defaultDomain, 2, true).saveOrUpdate();

        // some links between event 1 and positions
        EntityFactory.buildEventPosition(event1, position1).saveOrUpdate();
        EntityFactory.buildEventPosition(event1, position3).saveOrUpdate();
        EntityFactory.buildEventPosition(event1, position4).saveOrUpdate();

        // some links between event 2 and positions
        EntityFactory.buildEventPosition(event2, position1).saveOrUpdate();
        EntityFactory.buildEventPosition(event2, position3).saveOrUpdate();

        // event 1 should have 3 positions        
        assertEquals(3, positionRepository.findPositionsInEvent(event1).size());

        // event 2 should have 2 positions
        assertEquals(2, positionRepository.findPositionsInEvent(event2).size());
    }

    @Test
    public void testPositionAssignment()
    {
        // clear db
        HibernateUtil.clearAll();
        
        EventTemplate template = EntityFactory.buildEventTemplate("123").saveOrUpdate();
        
        // create helper
        Helper helper = EntityFactory.buildHelper("La", "Li", "", HelperState.ACTIVE, 1, 1, 1980);
        // create events
        Event evt2013 = EntityFactory.buildEvent("TRI-2013", "TRI-2013", 21, 6, 2013, EventState.FINISHED, template).saveOrUpdate();
        Event evt2014 = EntityFactory.buildEvent("TRI-2014", "TRI-2014", 21, 6, 2014, EventState.PLANNED, template).saveOrUpdate();
        // create positions
        Domain defaultDomain = SpeedyTestUtil.buildDefaultDomain(1);
        Position posA = EntityFactory.buildPosition("A", 12, defaultDomain, 0, true).saveOrUpdate();
        Position posB = EntityFactory.buildPosition("B", 13, defaultDomain, 1, true).saveOrUpdate();
        Position posC = EntityFactory.buildPosition("C", 14, defaultDomain, 2, true).saveOrUpdate();
        // event 2013 has positions (A,B,C)
        EntityFactory.buildEventPosition(evt2013, posA).saveOrUpdate();
        EntityFactory.buildEventPosition(evt2013, posB).saveOrUpdate();
        EntityFactory.buildEventPosition(evt2013, posC).saveOrUpdate();
        // event 2014 only has positions (A,B)
        EntityFactory.buildEventPosition(evt2014, posA).saveOrUpdate();
        EntityFactory.buildEventPosition(evt2014, posB).saveOrUpdate();

        // relate positions
        SpeedyRoutines.relatePositionsToEvent(evt2013, posA, posB, posC);
        SpeedyRoutines.relatePositionsToEvent(evt2014, posA, posB, posC);

        // assign positions
        EntityFactory.buildHelperAssignment(helper, evt2013, posA);
        EntityFactory.buildHelperAssignment(helper, evt2014, posC);
        // ...
        PositionService.isPositionAvailable(evt2014, posC);
    }
}