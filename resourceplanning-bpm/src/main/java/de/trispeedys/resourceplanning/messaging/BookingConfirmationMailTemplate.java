package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.interaction.HelperInteraction;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class BookingConfirmationMailTemplate extends AbstractMailTemplate
{
    private Helper helper;

    private Event event;

    private Position position;

    public BookingConfirmationMailTemplate(Helper aHelper, Event aEvent, Position aPosition)
    {
        super();
        this.helper = aHelper;
        this.event = aEvent;
        this.position = aPosition;
    }

    public String getBody()
    {
        String link =
                HelperInteraction.getBaseLink() +
                        "/AssignmentCancellationReceiver.jsp?helperId=" + helper.getId() + "&eventId=" + event.getId();
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak()
                .withParagraph(
                        "Du wurdest erfolgreich der Position '" +
                                position.getDescription() + "' zugeordnet. Falls Dir etwas dazwischenkommen sollte, kannst du diese Buchung " +
                                "mit dem untenstehenden Link stornieren :")
                .withLinebreak()
                .withLink(link, "Kündigen")
                .withLinebreak()
                .withParagraph("Deine Tri-Speedys.")
                .render();
    }

    public String getSubject()
    {
        return "Buchungsbestätigung";
    }
    
    public MessagingFormat getMessagingFormat()
    {
        return MessagingFormat.HTML;
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.BOOKING_CONFIRMATION;
    }
}