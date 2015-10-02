package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class SendReminderMailDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // find helper
        Helper helper =
                (Helper) DatasourceRegistry.getDatasource(null).findById(Helper.class,
                (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID));
        String helperId = String.valueOf((Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID));
        String eventId = String.valueOf((Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID));
        // write mail
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(),
                "Helfermeldung zum Triathlon 2016", generateReminderBody(helperId, eventId)).persist();
    }

    private String generateReminderBody(String helperId, String eventId)
    {
        StringBuffer buffer = new StringBuffer();
        for (HelperCallback value : HelperCallback.values())
        {
            buffer.append("http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/HelperCallbackReceiver.jsp?callbackResult=" +
                    value.toString() + "&helperId=" + helperId + "&eventId=" + eventId);
            buffer.append("\n\n");
            buffer.append("Eure Speedys");
        }
        return buffer.toString();
    }
}