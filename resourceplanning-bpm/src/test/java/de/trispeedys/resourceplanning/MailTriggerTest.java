package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class MailTriggerTest
{
    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();
    
    @SuppressWarnings("unchecked")
    //@Test
    @Deployment(resources = "MailTriggerProcess.bpmn")
    public void testSendAll()
    {
        HibernateUtil.clearAll();
        
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", "testhelper1.trispeedys@gmail.com", "M1", "M1", MessagingFormat.PLAIN).persist();
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", "testhelper1.trispeedys@gmail.com", "M2", "M2", MessagingFormat.PLAIN).persist();
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", "testhelper1.trispeedys@gmail.com", "M3", "M3", MessagingFormat.PLAIN).persist();
        
        processEngine.getRuntimeService().startProcessInstanceByKey("MailTriggerProcess");   
        
        assertEquals(3, DatasourceRegistry.getDatasource(MessageQueue.class).find(MessageQueue.class, "messagingState", MessagingState.PROCESSED).size());
    }
}