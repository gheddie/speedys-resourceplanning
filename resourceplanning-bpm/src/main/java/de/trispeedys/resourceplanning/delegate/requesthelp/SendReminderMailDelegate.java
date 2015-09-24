package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.builder.MessageQueueBuilder;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class SendReminderMailDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // find helper
        Helper helper =
                HibernateUtil.findById(Helper.class,
                        (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID));

        // write mail
        new MessageQueueBuilder().withFromAddress("noreply@tri-speedys.de")
                .withToAddress(helper.getEmail())
                .withSubject("123")
                .withBody("456")
                .build()
                .persist();
    }
}