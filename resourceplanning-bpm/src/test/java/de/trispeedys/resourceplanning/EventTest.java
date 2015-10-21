package de.trispeedys.resourceplanning;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.interaction.EventManager;
import de.trispeedys.resourceplanning.test.SpeedyRoutines;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import static org.junit.Assert.assertEquals;

public class EventTest
{
    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();
    
    //@Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testHelperProcesses()
    {
        // clear db
        HibernateUtil.clearAll();

        // there is a new event (with 7 active helpers)...
        Event event2016 =
                SpeedyRoutines.duplicateEvent(
                        TestDataGenerator.createRealLifeEvent("Triathlon 2016", "TRI-2016", 21, 6, 2016, EventState.FINISHED)
                                .getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);

        // start processes...
        EventManager.triggerHelperProcesses(EventTemplate.TEMPLATE_TRI);
        
        // there must be seven request processes
        assertEquals(7, processEngine.getRuntimeService().createExecutionQuery().list().size());
    }
}