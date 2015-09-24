package de.trispeedys.resourceplanning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "message_queue")
public class MessageQueue extends AbstractDbObject
{
    @Column(name = "from_address")
    private String fromAddress;
    
    @Column(name = "to_address")
    private String toAddress;
    
    private String subject;
    
    private String body;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "messaging_state")
    private MessagingState messagingState;
    
    public String getFromAddress()
    {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress)
    {
        this.fromAddress = fromAddress;
    }
    
    public String getToAddress()
    {
        return toAddress;
    }

    public void setToAddress(String toAddress)
    {
        this.toAddress = toAddress;
    }
    
    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    
    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }
    
    public MessagingState getMessagingState()
    {
        return messagingState;
    }
    
    public void setMessagingState(MessagingState messagingState)
    {
        this.messagingState = messagingState;
    }
}