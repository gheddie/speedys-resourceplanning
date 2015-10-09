package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.service.MessagingService;

public class MailingTest
{
    @SuppressWarnings("unchecked")
    //@Test
    public void testSendMails()
    {
        //clear db
        HibernateUtil.clearAll();
        
        //create message
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", "testhelper1.trispeedys@gmail.com", "Hallo", "Knallo", MessagingFormat.PLAIN).persist();
        
        //send
        MessagingService.sendAllUnprocessedMessages();
        
        //mail must have state 'PROCESSED'
        assertEquals(MessagingState.PROCESSED, ((MessageQueue) DatasourceRegistry.getDatasource(MessageQueue.class).findAll(MessageQueue.class).get(0)).getMessagingState());
    }
}