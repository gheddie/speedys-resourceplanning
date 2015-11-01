package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.util.HtmlGenerator;
import de.trispeedys.resourceplanning.util.configuration.AppConfiguration;
import de.trispeedys.resourceplanning.util.configuration.AppConfigurationValues;

public class BookingConfirmationMailTemplate extends AbstractMailTemplate
{
    private Helper helper;

    private Event event;

    // TODO finish implementation

    public BookingConfirmationMailTemplate(Helper aHelper, Event aEvent)
    {
        super();
        this.helper = aHelper;
        this.event = aEvent;
    }

    public String getBody()
    {
        String link =
                AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.HOST) +
                        "/resourceplanning-bpm-" +
                        AppConfiguration.getInstance().getConfigurationValue(AppConfigurationValues.VERSION) +
                        "/AssignmentCancellationReceiver.jsp?helperId=" + helper.getId() + "&eventId=" + event.getId();
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak()
                .withParagraph(
                        "Du wurdest erfolgreich der Position '" +
                                null + "' zugeordnet. Falls Dir etwas dazwischenkommen sollte, kannst du diese Buchung " +
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
}