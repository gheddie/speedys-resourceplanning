package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.messaging.DeactivationRecoveryMailTemplate;

public class SendDeactivationRequestDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Helper helper = getHelper(execution);
        DeactivationRecoveryMailTemplate template =
                new DeactivationRecoveryMailTemplate(getHelper(execution), getEvent(execution));
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(), template.constructSubject(), template.constructBody(),
                template.getMessagingType(), template.getMessagingFormat()).saveOrUpdate();
    }
}