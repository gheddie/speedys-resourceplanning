package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class SendDeactivationRequestDelegate implements JavaDelegate
{
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) throws Exception
    {
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Helper helper = (Helper) DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, helperId);
        // write mail
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(),
                "Helfermeldung zum Triathlon 2016", generateDeactivationRequestBody(), MessagingType.DEACTIVATION_REQUEST).persist();
    }

    private String generateDeactivationRequestBody()
    {
        return "123";
    }
}