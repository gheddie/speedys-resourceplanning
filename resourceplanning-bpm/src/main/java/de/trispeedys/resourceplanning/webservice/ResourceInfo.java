package de.trispeedys.resourceplanning.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.camunda.bpm.BpmPlatform;

import de.trispeedys.resourceplanning.dto.EventCommitmentDTO;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.service.MessagingService;
import de.trispeedys.resourceplanning.variables.BpmVariables;

@SuppressWarnings("restriction")
@WebService
@SOAPBinding(style = Style.RPC)
public class ResourceInfo
{
    public EventCommitmentDTO[] queryCommitments(String firstName, String lastName)
    {
        List<EventCommitmentDTO> commitmentList = new ArrayList<EventCommitmentDTO>();
        return commitmentList.toArray(new EventCommitmentDTO[commitmentList.size()]);
    }
    
    public void startHelperRequestProcess(Long helperId, Long eventId, String businessKey)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, new Long(helperId));
        variables.put(BpmVariables.RequestHelpHelper.VAR_EVENT_ID, new Long(eventId));
        BpmPlatform.getDefaultProcessEngine().getRuntimeService().startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG,
                businessKey, variables);
    }
    
    public void testExternalCall()
    {
        System.out.println("boohoo - this is an external call!!");
        System.out.println("process engine is executing " + BpmPlatform.getDefaultProcessEngine().getRuntimeService().createProcessInstanceQuery().list().size() + " process instances.");
    }
    
    public void sendMessages()
    {
        System.out.println("sending all messages...");
        MessagingService.sendAllUnprocessedMessages();
    }
}