package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.util.MailSender;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class SendReminderMailDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // find helper and event
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Long eventId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID);
        // write mail
        Helper helper = (Helper) DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, helperId);
        Event event = (Event) DatasourceRegistry.getDatasource(Event.class).findById(Event.class, eventId);
        sendReminderMail(helper, event, (Integer) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS));
        //increase attempts
        int oldValue = (Integer) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS);
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS, (oldValue+1));
    }

    private void sendReminderMail(Helper helper, Event event, int attemptCount)
    {
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(),
                "Helfermeldung zum " + event.getDescription(), generateReminderBody(helper, event), getMessagingType(attemptCount)).persist();
    }

    private MessagingType getMessagingType(int attempt)
    {
        switch (attempt)
        {
            case 0:                
                return MessagingType.REMINDER_STEP_0;
            case 1:                
                return MessagingType.REMINDER_STEP_1;
            case 2:                
                return MessagingType.REMINDER_STEP_2;
            default:
                return MessagingType.NONE;
        }
    }

    @SuppressWarnings("unchecked")
    private String generateReminderBody(Helper helper, Event event)
    {
        //build message body
        StringBuffer buffer = new StringBuffer();
        buffer.append("Hallo, " + helper.getFirstName() + "!!");
        buffer.append("\n\n");
        buffer.append("Bitte sag uns, was Du beim anstehenden "+event.getDescription()+" tun m�chtest:");
        buffer.append("\n\n");
        for (HelperCallback value : HelperCallback.values())
        {
            buffer.append("http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/HelperCallbackReceiver.jsp?callbackResult=" +
                    value.toString() + "&helperId=" + helper.getId() + "&eventId=" + event.getId());
            buffer.append("\n\n");
        }
        buffer.append("Eure Speedys");
        return buffer.toString();
    }
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args)
    {
        Event event = (Event) DatasourceRegistry.getDatasource(Event.class).findById(Event.class, new Long(3279));
        Helper helper = (Helper) DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, new Long(3257));
        
//        new SendReminderMailDelegate().sendReminderMail(helper, event, 0);
        
        MailSender.sendHtmlMail(helper.getId(), event.getId());
    }
}