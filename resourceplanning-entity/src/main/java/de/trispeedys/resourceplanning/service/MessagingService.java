package de.trispeedys.resourceplanning.service;

import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;
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
            MailSender.sendMail(message.getToAddress(), message.getBody(), message.getSubject());
            message.setMessagingState(MessagingState.PROCESSED);
            DatasourceRegistry.getDatasource(MessageQueue.class).saveOrUpdate(message);
        }
    }
}