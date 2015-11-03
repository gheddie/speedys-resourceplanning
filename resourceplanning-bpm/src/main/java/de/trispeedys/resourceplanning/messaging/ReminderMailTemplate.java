package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;

// TODO use it (in SendReminderMailDelegate) !!
public class ReminderMailTemplate extends AbstractMailTemplate
{
    public String getBody()
    {
        return null;
    }

    public String getSubject()
    {
        return null;
    }

    public MessagingFormat getMessagingFormat()
    {
        return null;
    }

    public MessagingType getMessagingType()
    {
        return null;
    }
}