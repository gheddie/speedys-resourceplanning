package de.trispeedys.resourceplanning;

import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintViolationException;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

/**
 * Tests the unique criterias for the {@link EventCommitment} entity.
 * 
 * @author Stefan.Schulz
 *
 */
public class EventCommitmentUniqueTest
{
    /**
     * Combination of {@link EventCommitment#getEvent()} and {@link EventCommitment#getPosition()} MUST be unique.
     * Otherwise, it would mean that a {@link Position} is assigned twice or more in the same {@link Event}.
     */
    @Test(expected = ConstraintViolationException.class)
    public void testEventAndPositionUnique()
    {
        // clear db
        HibernateUtil.clearAll();

        Position position = EntityFactory.buildPosition("Some position", 12, null, false).persist();
        Event event = null;
        Helper helper1 =
                EntityFactory.buildHelper("Stefan", "Schulz", "a@b.de", HelperState.ACTIVE, 13, 2, 1976).persist();
        Helper helper2 =
                EntityFactory.buildHelper("Diana", "Schulz", "a@b.de", HelperState.ACTIVE, 4, 3, 1973).persist();
        EntityFactory.buildEventCommitment(helper1, event, position).persist();
        EntityFactory.buildEventCommitment(helper2, event, position).persist();
    }
}