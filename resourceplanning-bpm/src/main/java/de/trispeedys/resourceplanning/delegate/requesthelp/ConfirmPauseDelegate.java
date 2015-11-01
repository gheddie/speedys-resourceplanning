package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpDelegate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.messaging.ConfirmPauseMailTemplate;

public class ConfirmPauseDelegate extends RequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // send mail
        Helper helper = getHelper(execution);
        ConfirmPauseMailTemplate template =
                new ConfirmPauseMailTemplate(helper);
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(), template.getSubject(),
                template.getBody(), MessagingType.PAUSE_CONFIRM, MessagingFormat.HTML).saveOrUpdate();
    }
}