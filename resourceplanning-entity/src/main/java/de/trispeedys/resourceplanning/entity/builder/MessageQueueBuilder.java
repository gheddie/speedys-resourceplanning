package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;

public class MessageQueueBuilder extends AbstractEntityBuilder<MessageQueue>
{
    private String fromAddress;
    
    private String toAddress;
    
    private String subject;
    
    private String body;

    private MessagingState messagingState;

    public MessageQueueBuilder withFromAddress(String aFromAddress)
    {
        fromAddress = aFromAddress;
        return this;
    }

    public MessageQueueBuilder withToAddress(String aToAddress)
    {
        toAddress = aToAddress;
        return this;
    }

    public MessageQueueBuilder withSubject(String aSubject)
    {
        subject = aSubject;
        return this;
    }

    public MessageQueueBuilder withBody(String aBody)
    {
        body = aBody;
        return this;
    }
    
    public MessageQueueBuilder withMessagingState(MessagingState aMessagingState)
    {
        messagingState = aMessagingState;
        return this;
    }
    
    public MessageQueue build()
    {
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setFromAddress(fromAddress);
        messageQueue.setToAddress(toAddress);
        messageQueue.setSubject(subject);
        messageQueue.setBody(body);
        if (messagingState == null)
        {
            messagingState = MessagingState.UNPROCESSED;
        }
        messageQueue.setMessagingState(messagingState);
        return messageQueue;
    }

}