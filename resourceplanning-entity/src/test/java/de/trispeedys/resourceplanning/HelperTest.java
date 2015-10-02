package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.DataModelUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.service.HelperService;
import de.trispeedys.resourceplanning.test.TestDataProvider;

public class HelperTest
{
    @Test
    public void testFirstAssignment()
    {
        HibernateUtil.clearAll();
        Helper helper =
                EntityFactory.buildHelper("Stefan", "Schulz", EventCommitmentTest.TEST_MAIL_ADDRESS,
                        HelperState.ACTIVE, 13, 2, 1976).persist();
        assertEquals(true, HelperService.isFirstAssignment(helper.getId()));
    }

    /**
     * A helper can have two assignments in one event!!
     */
    @Test
    public void testDuplicateAssignment()
    {
        //clear db
        HibernateUtil.clearAll();
        // create helper
        Helper helper = EntityFactory.buildHelper("Diana", "Schulz", "a@b.de", HelperState.ACTIVE, 4, 3, 1973).persist();
        // create domain
        Domain domain = EntityFactory.buildDomain("someDomain", 1, null).persist();
        //create positions
        Position pos1 = EntityFactory.buildPosition("Nudelparty", 12, domain).persist();
        Position pos2 = EntityFactory.buildPosition("Laufstrecke", 12, domain).persist();
        //create event
        Event tri2014 = EntityFactory.buildEvent("Triathlon 2014", "TRI-2014", 21, 6, 2014).persist();
        //assign positions to that event
        DataModelUtil.relatePositionsToEvent(tri2014, pos1, pos2);        
        //assign helper to both positions
        DataModelUtil.assignHelperToPositions(helper, tri2014, pos1, pos2);
    }
    
    @Test
    public void testSelectActiveHelperIds()
    {        
        HibernateUtil.clearAll();        
        TestDataProvider.createSimpleEvent("TRI", "TRI", 1, 1, 1980);       
        assertEquals(5, HelperService.queryActiveHelperIds().size());
    }
}