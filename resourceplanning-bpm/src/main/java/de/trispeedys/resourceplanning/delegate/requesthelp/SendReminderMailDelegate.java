package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.CallbackChoiceGenerator;
import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpDelegate;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.util.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.util.configuration.AppConfigurationValues;

public class SendReminderMailDelegate extends RequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // find helper and event
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Long eventId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID);
        Long positionId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_PRIOR_POSITION);
        // write mail
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findById(helperId);
        Event event = (Event) Datasources.getDatasource(Event.class).findById(eventId);
        Position position = (Position) Datasources.getDatasource(Position.class).findById(positionId);
        sendReminderMail(helper, event, position, (Integer) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS), execution);
        // increase attempts
        int oldValue = (Integer) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS);
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS, (oldValue + 1));
    }

    private void sendReminderMail(Helper helper, Event event, Position position, int attemptCount, DelegateExecution execution)
    {
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(), "Helfermeldung zum " + event.getDescription(), generateReminderBody(helper, event, position, execution),
                getMessagingType(attemptCount), MessagingFormat.HTML).saveOrUpdate();
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

    private String generateReminderBody(Helper helper, Event event, Position position, DelegateExecution execution)
    {
        // build message body
        StringBuffer buffer = new StringBuffer();
        buffer.append("Hallo, " + helper.getFirstName() + "!!");
        buffer.append("<br><br>");
        buffer.append("Beim letzten Event warst du auf der Position '" + position.getDescription() + "' eingesetzt.");
        if (!((Boolean) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_PRIOR_POS_AVAILABLE)))
        {
            buffer.append("<br><br>");
            buffer.append("Diese Position ist leider nicht mehr verfügbar.");
        }
        else
        {
            buffer.append("<br><br>");
            buffer.append("Diese Position auch dieses Mal wieder zu besetzen.");
        }
        buffer.append("<br><br>");
        buffer.append("Bitte sag uns, was Du beim anstehenden " + event.getDescription() + " tun möchtest:");
        buffer.append("<br><br>");
        for (HelperCallback callback : new CallbackChoiceGenerator().generateChoices(helper, getEvent(execution)))
        {
            buffer.append(renderCallbackOption(helper, event, callback));
            buffer.append("<br><br>");
        }
        buffer.append("Eure Speedys");
        return buffer.toString();
    }

    private String renderCallbackOption(Helper helper, Event event, HelperCallback helperCallback)
    {
        return "<a href=\"" +
                AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.HOST) + "/resourceplanning-bpm-" +
                AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.VERSION) + "/HelperCallbackReceiver.jsp?callbackResult=" + helperCallback + "&helperId=" + helper.getId() +
                "&eventId=" + event.getId() + "\">" + helperCallback.getDescription() + "</a>";
    }
}