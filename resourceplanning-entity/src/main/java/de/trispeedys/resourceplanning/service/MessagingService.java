package de.trispeedys.resourceplanning.service;

import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.util.MailSender;

public class MessagingService
{
    @SuppressWarnings("unchecked")
    public static List<MessageQueue> findAllUnprocessedMessages()
    {
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put("messagingState", MessagingState.UNPROCESSED);
        return (List<MessageQueue>) DatasourceRegistry.getDatasource(null).find(
                "FROM " + MessageQueue.class.getSimpleName() + " mq WHERE mq.messagingState = :messagingState",
                variables);
    }

    @SuppressWarnings("unchecked")
    public static void sendAllUnprocessedMessages()
    {
        LoggerService.log("sending all unprocessed messages...");
        
        for (MessageQueue message : findAllUnprocessedMessages())
        {
            try
            {
                switch (message.getMessagingFormat())
                {
                    case PLAIN:
                        MailSender.sendMail(message.getToAddress(), message.getBody(), message.getSubject());
                        break;
                    case HTML:
                        MailSender.sendHtmlMail(message.getToAddress(), message.getBody(), message.getSubject());
                        break;                   
                }
                message.setMessagingState(MessagingState.PROCESSED);                   
            }
            catch (Exception e)
            {
                message.setMessagingState(MessagingState.FAILURE);
            }
            finally
            {
                DatasourceRegistry.getDatasource(MessageQueue.class).saveOrUpdate(message);
            }
        }
    }
    
    public static void createMessage(String fromAddress, String toAddress, String subject, String body, MessagingType messagingType, MessagingFormat messagingFormat)
    {
        EntityFactory.buildMessageQueue(fromAddress, toAddress,
                subject, body, messagingType, messagingFormat).persist();
    }
}