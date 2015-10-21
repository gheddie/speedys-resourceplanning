package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.test.SpeedyRoutines;
import de.trispeedys.resourceplanning.test.TestDataGenerator;

public class CallbackChoiceGeneratorTest
{
    /**
     * Helper was assigned to a position in 2015, and the event 2016 is all new, so he has all the choices...
     */
    @Test
    public void testAllChoices()
    {
        HibernateUtil.clearAll();

        // create events
        Event event2015 = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015);
        Event event2016 = SpeedyRoutines.duplicateEvent(event2015.getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        
        // get helper
        Helper helper =
                (Helper) Datasources.getDatasource(Helper.class).findAll().get(0);
        
        assertTrue(checkChoices(HelperCallback.values(),
                new CallbackChoiceGenerator().generateChoices(helper, event2016)));
    }
    
    /**
     * Helper 'A' was assigned to a position in 2015, and in 2016, his position is already blocked (by helper 'B'),
     * so {@link HelperCallback#ASSIGNMENT_AS_BEFORE} must be not available...
     */
    @Test
    public void testPriorPositionAlreadyAssigned()
    {
        HibernateUtil.clearAll();

        // create events
        Event event2015 = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015);
        Event event2016 = SpeedyRoutines.duplicateEvent(event2015.getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        
        // get helpers
        Helper helperA =
                (Helper) Datasources.getDatasource(Helper.class).findAll().get(0);
        Helper helperB =
                (Helper) Datasources.getDatasource(Helper.class).findAll().get(1);
        
        // assign 'B' to 'A's prior position...
        AssignmentService.assignHelper(helperB, event2016, AssignmentService.getPriorAssignment(helperA, event2015.getEventTemplate()).getPosition());
        
        assertTrue(checkChoices(new HelperCallback[] {HelperCallback.CHANGE_POS, HelperCallback.PAUSE_ME},
                new CallbackChoiceGenerator().generateChoices(helperA, event2016)));
    }
    
    //@Test
    public void testChoicesWennDieVorherigePositionInDiesemEeventNichtMehrDa()
    {
        HibernateUtil.clearAll();

        // create events
        Event event2015 = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015);
        Event event2016 = SpeedyRoutines.duplicateEvent(event2015.getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        
        // remove prior position of helper 'A' from event 2016        
        
        assertTrue(1 == 3);
    }

    // ---

    private boolean checkChoices(HelperCallback[] expected, List<HelperCallback> generatedChoices)
    {
        if ((expected == null) && (generatedChoices == null))
        {
            return true;
        }
        else if ((expected != null) && (generatedChoices == null))
        {
            return false;
        }
        else if ((expected == null) && (generatedChoices != null))
        {
            return false;
        }
        else
        {
            if (expected.length != generatedChoices.size())
            {
                return false;
            }
            else
            {
                for (HelperCallback expectedCallback : Arrays.asList(expected))
                {
                    if (!(generatedChoices.contains(expectedCallback)))
                    {
                        return false;
                    }
                }
                return true;
            }
        }
    }
}