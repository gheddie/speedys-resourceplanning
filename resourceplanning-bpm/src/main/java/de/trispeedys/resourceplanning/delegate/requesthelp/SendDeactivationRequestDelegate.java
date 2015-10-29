package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmVariables;

public class SendDeactivationRequestDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findById(helperId);
        // write mail
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(),
                "Helfermeldung zum Triathlon 2016", generateDeactivationRequestBody(), MessagingType.DEACTIVATION_REQUEST, MessagingFormat.PLAIN).saveOrUpdate();
    }

    private String generateDeactivationRequestBody()
    {
        return "123";
    }
}