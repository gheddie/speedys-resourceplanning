package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.AlertCancellationMailTemplate;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.util.configuration.AppConfigurationValues;

public class AlertCancellationDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Helper helper = getHelper(execution);
        Position position =
                RepositoryProvider.getRepository(PositionRepository.class).findById(
                        (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION));
        AlertCancellationMailTemplate template = new AlertCancellationMailTemplate(helper, getEvent(execution), position);
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de",
                AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.ADMIN_MAIL), template.getSubject(),
                template.getBody(), template.getMessagingType(), template.getMessagingFormat()).saveOrUpdate();
    }
}