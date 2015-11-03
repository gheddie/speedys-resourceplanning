package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.BookingConfirmationMailTemplate;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public class BookingConfirmationDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Helper helper = getHelper(execution);
        BookingConfirmationMailTemplate template =
                new BookingConfirmationMailTemplate(getHelper(execution), getEvent(execution), RepositoryProvider.getRepository(
                        PositionRepository.class).findById(
                        (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION)));
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(), template.getSubject(), template.getBody(),
                template.getMessagingType(), template.getMessagingFormat()).saveOrUpdate();
    }
}