package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.LoggerService;
import de.trispeedys.resourceplanning.util.configuration.AppConfiguration;

public class CheckPriorAssignmentsDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        AppConfiguration.getInstance();
        
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findById(helperId);
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