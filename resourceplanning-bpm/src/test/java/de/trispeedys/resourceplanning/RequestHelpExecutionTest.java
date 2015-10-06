package de.trispeedys.resourceplanning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.service.CommitmentService;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.test.DatabaseRoutines;
import de.trispeedys.resourceplanning.test.TestDataProvider;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.variables.BpmVariables;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RequestHelpExecutionTest
{
    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();
    
    /**
     * - (0) clear database
     * - (1) prepare event with helpers assigned for 2015 (all positions are assigned, same count of positions and helpers)
     * - (2) duplicate event (for 2016)
     * - (3) assign the position which was assigned to helper 'A' in 2015 to helper 'B' in 2016
     * - (4) start process for helper 'A' and event in 2016 who was assigned to that position in the year before (2015)
     * - (5) let helper 'A' state he wants to be assigned as before (by sending message 'MSG_HELP_CALLBACK' with paramter {@link HelperCallback#ASSIGNMENT_AS_BEFORE})
     * - (6) check availability must fail as the position is already assigned (to 'B')
     * - (7) mail with other positions (4) must be generated and sent to 'A'
     * - (8) helper 'A' chooses one of that positions ('C')
     * - (9) chosen position ('C') must be assigned to the helper 'A' afterwards
     * - (10) process must be finished
     */
    @SuppressWarnings("unchecked")
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testProposePositionsOnAssignmentRequestedAsBefore()
    {
        // (0)
        HibernateUtil.clearAll();
        
        // (1)
        Event event2015 = TestDataProvider.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015);
        
        // (2)
        Event event2016 = DatabaseRoutines.duplicateEvent(event2015.getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        
        // (3)
        List<Helper> allHelpers = DatasourceRegistry.getDatasource(Helper.class).findAll(Helper.class);
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);
        Helper helperB = allHelpers.get(3);
        //get assigned position for helper 'A' in 2015
        EventCommitment commitmentA2015 = CommitmentService.getHelperCommitment(helperA, event2015);
        CommitmentService.confirmHelper(helperB, event2016, commitmentA2015.getPosition());
        
        // (4)
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, rule);
        
        // (5)
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
        rule.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
        
        // (6) --> if CheckAvailabiliy-Delegate fails, variable 'posAvailable' must be set to 'false'
        // ??? how to check the variable ???
        
        // (7) --> mails with types 'MessagingType.REMINDER_STEP_0' and 'MessagingType.PROPOSE_POSITIONS' must be there...
        assertEquals(4, PositionService.findUnassignedPositionsInEvent(event2016).size());
        assertTrue(RequestHelpTestUtil.checkMails(2, MessagingType.REMINDER_STEP_0, MessagingType.PROPOSE_POSITIONS));
    }
}