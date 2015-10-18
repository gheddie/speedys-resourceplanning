package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.test.EventRoutines;
import de.trispeedys.resourceplanning.test.TestDataProvider;

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
        Event event2015 = TestDataProvider.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015);
        Event event2016 =
                EventRoutines.duplicateEvent(event2015.getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        
        // get helper
        Helper helper =
                DatasourceRegistry.getDatasource(Helper.class).findAll(Helper.class).get(0);
        
        assertTrue(checkChoices(HelperCallback.values(),
                new CallbackChoiceGenerator().generateChoices(helper)));
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