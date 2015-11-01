package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpDelegate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.messaging.AlertCancellationMailTemplate;

public class AlertCancellationDelegate extends RequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Helper helper = getHelper(execution);
        AlertCancellationMailTemplate template =
                new AlertCancellationMailTemplate(
                        helper,
                        getEvent(execution));
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(), template.getSubject(),
                template.getBody(), MessagingType.ALERT_BOOKING_CANCELLED, MessagingFormat.HTML).saveOrUpdate();
    }
}