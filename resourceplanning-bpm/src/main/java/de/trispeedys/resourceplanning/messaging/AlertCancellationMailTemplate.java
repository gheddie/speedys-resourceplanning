package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;

public class AlertCancellationMailTemplate extends AbstractMailTemplate
{
    private Helper helper;

    private Event event;

    private Position position;

    public AlertCancellationMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super();
        this.helper = aHelper;
        this.event = aEvent;
        this.position = aPosition;
    }

    public String getBody()
    {
        // TODO use proper mail body !!
        return "Helfer "+helper.getLastName()+", "+helper.getFirstName()+" (Position: "+position.getDescription()+") hat abgesagt!!";
    }

    public String getSubject()
    {
        return "Helper-Absage für den Wettkampf " + event.getDescription();
    }
    
    public MessagingFormat getMessagingFormat()
    {
        return MessagingFormat.HTML;
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.ALERT_BOOKING_CANCELLED;
    }
}