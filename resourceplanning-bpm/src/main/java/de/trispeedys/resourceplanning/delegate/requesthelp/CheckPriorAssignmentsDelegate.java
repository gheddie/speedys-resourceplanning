package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.LoggerService;
import de.trispeedys.resourceplanning.util.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class CheckPriorAssignmentsDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        AppConfiguration.getInstance();
        
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Helper helper = (Helper) DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, helperId);
        boolean firstAssignment = AssignmentService.isFirstAssignment(helperId);
        if (firstAssignment)
        {
            LoggerService.log(execution.getBusinessKey(), "this is the first assignments for helper '"+helper+"'.");
        }
        else
        {
            LoggerService.log(execution.getBusinessKey(), "this is NOT the first assignments for helper '"+helper+"'.");   
        }        
        execution.setVariable(
                BpmVariables.RequestHelpHelper.VAR_FIRST_ASSIGNMENT,
                firstAssignment);
        //set mails attempts to 0
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS, 0);
    }
}