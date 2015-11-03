package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class CancelConfirmationMailTemplate extends AbstractMailTemplate
{
    private Helper helper;

    private Event event;

    private Position position;

    public CancelConfirmationMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super();
        this.helper = aHelper;
        this.event = aEvent;
        this.position = aPosition;
    }

    public String getBody()
    {
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak()
                .withParagraph("Du hast Deinen Einsatz auf der Position '" + position.getDescription() + "' erfolgreich gekündigt.")
                .withLinebreak()
                .withParagraph("Deine Tri-Speedys.")
                .render();
    }

    public String getSubject()
    {
        return "Bestätigung Deiner Absage";
    }
    
    public MessagingFormat getMessagingFormat()
    {
        return MessagingFormat.HTML;
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.CANCELLATION_CONFIRM;
    }
}