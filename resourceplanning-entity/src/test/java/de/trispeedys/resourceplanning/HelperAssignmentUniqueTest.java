package de.trispeedys.resourceplanning;

import javax.validation.ConstraintViolationException;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.DataModelUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

/**
 * Tests the unique criterias for the {@link HelperAssignment} entity.
 * 
 * @author Stefan.Schulz
 *
 */
public class HelperAssignmentUniqueTest
{
    /**
     * Combination of {@link HelperAssignment#getEvent()} and {@link HelperAssignment#getPosition()} MUST be unique.
     * Otherwise, it would mean that a {@link Position} is assigned twice or more in the same {@link Event}.
     */
    @Test(expected = org.hibernate.exception.ConstraintViolationException.class)
    public void testEventAndPositionUnique()
    {
        // clear db
        HibernateUtil.clearAll();
        
        // create domain
        Domain domain1 = EntityFactory.buildDomain("dom1", 1).persist();

        Position position = EntityFactory.buildPosition("Some position", 12, domain1, false).persist();
        Event event = EntityFactory.buildEvent("Triathlon 2016", "TRI-2016", 21, 6, 2016, EventState.PLANNED).persist();
        Helper helper1 =
                EntityFactory.buildHelper("Stefan", "Schulz", "a@b.de", HelperState.ACTIVE, 13, 2, 1976).persist();
        Helper helper2 =
                EntityFactory.buildHelper("Diana", "Schulz", "a@b.de", HelperState.ACTIVE, 4, 3, 1973).persist();
        
        //assign position to event to avoid resource planning exception
        DataModelUtil.relatePositionsToEvent(event, position);
        
        EntityFactory.buildHelperAssignment(helper1, event, position).persist();
        EntityFactory.buildHelperAssignment(helper2, event, position).persist();
    }
}