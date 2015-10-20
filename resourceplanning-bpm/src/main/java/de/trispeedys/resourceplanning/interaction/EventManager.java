package de.trispeedys.resourceplanning.interaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.test.ProcessEngineRule;

import de.trispeedys.resourceplanning.entity.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.service.EventService;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class EventManager
{
    public static void triggerHelperProcesses(String templateName)
    {
        if (StringUtil.isBlank(templateName))
        {
            throw new ResourcePlanningException("template name must not be blank!!");
        }
        
        List<Event> events = EventService.findEventsByTemplateAndStatus(templateName, EventState.PLANNED);
        
        if ((events == null) || (events.size() != 1))
        {
            throw new ResourcePlanningException("there must be exactly one planned event of template '"+templateName+"'!!");
        }
        // start request process for every active helper...
        List<Helper> activeHelpers = Datasources.getDatasource(Helper.class).find(Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        for (Helper helper : activeHelpers)
        {
            startHelperRequestProcess(helper, events.get(0));
        }
    }
    
    private static void startHelperRequestProcess(Helper helper, Event event)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, new Long(helper.getId()));
        variables.put(BpmVariables.RequestHelpHelper.VAR_EVENT_ID, new Long(event.getId()));
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event.getId());
        BpmPlatform.getDefaultProcessEngine().getRuntimeService().startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG,
                businessKey, variables);
    }
}