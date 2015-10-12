package de.trispeedys.resourceplanning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.misc.MessagingState;

@Entity
@Table(name = "message_queue")
public class MessageQueue extends AbstractDbObject
{
    @Column(name = "from_address")
    private String fromAddress;
    
    @Column(name = "to_address")
    private String toAddress;
    
    @NotNull
    private String subject;
    
    @NotNull
    private String body;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "messaging_state")
    private MessagingState messagingState;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "messaging_type")
    private MessagingType messagingType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "messaging_format")
    private MessagingFormat messagingFormat;
    
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
    
    public MessagingType getMessagingType()
    {
        return messagingType;
    }
    
    public void setMessagingType(MessagingType messagingType)
    {
        this.messagingType = messagingType;
    }
    
    public MessagingFormat getMessagingFormat()
    {
        return messagingFormat;
    }
    
    public void setMessagingFormat(MessagingFormat messagingFormat)
    {
        this.messagingFormat = messagingFormat;
    }
    
    public String toString()
    {
        return getClass().getSimpleName() + " ["+messagingType+"]";
    }
}