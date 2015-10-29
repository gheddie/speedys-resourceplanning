package de.trispeedys.resourceplanning.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.task.Task;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.dto.EventDTO;
import de.trispeedys.resourceplanning.dto.HelperAssignmentDTO;
import de.trispeedys.resourceplanning.dto.HelperDTO;
import de.trispeedys.resourceplanning.dto.HierarchicalEventItemDTO;
import de.trispeedys.resourceplanning.dto.ManualAssignmentDTO;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.DbLogLevel;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.execution.BpmSignals;
import de.trispeedys.resourceplanning.execution.BpmTaskDefinitionKeys;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.interaction.EventManager;
import de.trispeedys.resourceplanning.interaction.HelperInteraction;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.LoggerService;
import de.trispeedys.resourceplanning.service.MessagingService;
import de.trispeedys.resourceplanning.util.EntityTreeNode;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.StringUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

@SuppressWarnings("restriction")
@WebService
@SOAPBinding(style = Style.RPC)
public class ResourceInfo
{
    public HelperAssignmentDTO[] queryHelperAssignments(String firstName, String lastName)
    {
        List<HelperAssignmentDTO> helperAssignmentList = new ArrayList<HelperAssignmentDTO>();
        return helperAssignmentList.toArray(new HelperAssignmentDTO[helperAssignmentList.size()]);
    }

    public void assignHelper(Long helperId, Long positionId, Long eventId)
    {
        AssignmentService.assignHelper(
                RepositoryProvider.getRepository(HelperRepository.class).findById(helperId),
                RepositoryProvider.getRepository(EventRepository.class).findById(helperId),
                RepositoryProvider.getRepository(PositionRepository.class).findById(helperId));
    }

    public void sendAllMessages()
    {
        MessagingService.sendAllUnprocessedMessages();
    }

    public void processHelperCallback(String businessKey, String callback)
    {
        if ((businessKey == null) || (businessKey.length() == 0))
        {
            System.out.println("business key must be set --> returning.");
            return;
        }
        HelperCallback callbackValue = HelperCallback.valueOf(callback);
        if ((callback == null) || (callback.length() == 0) || (callbackValue == null))
        {
            System.out.println("string '' can not be interpreted as helper callback --> returning.");
            return;
        }
        LoggerService.log("processed helper callback '" +
                callbackValue + "' for business key '" + businessKey + "'...", DbLogLevel.INFO);
        HelperInteraction.processReminderCallback(callbackValue, businessKey);
    }

    public void startProcessesForActiveHelpersByTemplateName(String templateName)
    {
        EventManager.triggerHelperProcesses(templateName);
    }

    public void startProcessesForActiveHelpersByEventId(Long eventId)
    {
        EventManager.triggerHelperProcesses(eventId);
    }

    public void finishUp()
    {
        BpmPlatform.getDefaultProcessEngine()
                .getRuntimeService()
                .signalEventReceived(BpmSignals.RequestHelpHelper.SIG_EVENT_STARTED);
    }

    public EventDTO[] queryEvents()
    {
        List<Event> allEvents = Datasources.getDatasource(Event.class).findAll();
        List<EventDTO> dtos = new ArrayList<EventDTO>();
        EventDTO dto = null;
        for (Event event : allEvents)
        {
            dto = new EventDTO();
            dto.setDescription(event.getDescription());
            dto.setEventId(event.getId());
            dtos.add(dto);
        }
        return dtos.toArray(new EventDTO[dtos.size()]);
    }

    public void duplicateEvent(Long eventId, String description, String eventKey, int day, int month, int year)
    {
        if (eventId == null)
        {
            throw new ResourcePlanningException("event id must not be null!!");
        }
        Event event = Datasources.getDatasource(Event.class).findById(eventId);
        if (event == null)
        {
            throw new ResourcePlanningException("event with id '" + eventId + "' could not found!!");
        }
        SpeedyRoutines.duplicateEvent(event, description, eventKey, day, month, year, null, null);
    }

    public HierarchicalEventItemDTO[] getEventNodes(Long eventId, boolean onlyUnassignedPositions)
    {
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        List<EntityTreeNode> nodes = SpeedyRoutines.flattenedEventNodes(event);
        List<HierarchicalEventItemDTO> dtos = new ArrayList<HierarchicalEventItemDTO>();
        HierarchicalEventItemDTO dto = null;
        for (EntityTreeNode node : nodes)
        {
            dto = new HierarchicalEventItemDTO();
            dto.setItemType(node.getItemType().toString());
            dto.setInfoString(node.infoString());
            dto.setHierarchyLevel(node.getHierarchyLevel());
            dto.setItemKey(node.itemKey());
            dto.setAssignmentString(node.getAssignmentString());
            dtos.add(dto);
        }
        return dtos.toArray(new HierarchicalEventItemDTO[dtos.size()]);
    }

    public HelperDTO[] queryHelpers()
    {
        List<HelperDTO> dtos = new ArrayList<HelperDTO>();
        HelperDTO dto = null;
        for (Helper helper : RepositoryProvider.getRepository(HelperRepository.class).findAll())
        {
            dto = new HelperDTO();
            dto.setLastName(helper.getLastName());
            dto.setFirstName(helper.getFirstName());
            dto.setEmail(helper.getEmail());
            dto.setCode(helper.getCode());
            dtos.add(dto);
        }
        return dtos.toArray(new HelperDTO[dtos.size()]);
    }

    public ManualAssignmentDTO[] queryManualAssignments()
    {
        List<ManualAssignmentDTO> dtos = new ArrayList<ManualAssignmentDTO>();
        ManualAssignmentDTO dto = null;
        for (Task manualAssignmentTask : BpmPlatform.getDefaultProcessEngine()
                .getTaskService()
                .createTaskQuery()
                .taskDefinitionKey(
                        BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT)
                .list())
        {
            dto = new ManualAssignmentDTO();
            dto.setMoo(manualAssignmentTask.getId());            
            dtos.add(dto);
        }
        return dtos.toArray(new ManualAssignmentDTO[dtos.size()]);
    }
    
    public void completeManualAssignment(String taskId, Long positionId)
    {
        if ((taskId == null) || (StringUtil.isBlank(taskId)))
        {
            throw new ResourcePlanningException("task id must be set in order to complete manual assignment!!");
        }
        if (positionId == null)
        {
            throw new ResourcePlanningException("position id must be set in order to complete manual assignment!!");
        }        
        System.out.println("completing manual assignment [taskId:"+taskId+"|positionId:"+positionId+"]");
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, positionId);
        BpmPlatform.getDefaultProcessEngine().getTaskService().complete(taskId, variables);
    }
}