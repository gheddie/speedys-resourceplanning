package de.trispeedys.resourceplanning.repository;

import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.MessageQueueDatasource;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;

public class MessageQueueRepository implements DatabaseRepository<MessageQueueRepository>
{
    private MessageQueueDatasource datasource;
    
    public List<MessageQueue> findAllUnprocessedMessages()
    {
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put("messagingState", MessagingState.UNPROCESSED);
        return datasource.find(
                "FROM " + MessageQueue.class.getSimpleName() + " mq WHERE mq.messagingState = :messagingState",
                variables);
    }

    public void createDataSource()
    {
        datasource = new MessageQueueDatasource();
    }
}