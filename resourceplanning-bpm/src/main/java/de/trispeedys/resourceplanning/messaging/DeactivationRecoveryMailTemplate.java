package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.interaction.HelperInteraction;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class DeactivationRecoveryMailTemplate extends AbstractMailTemplate
{
    private Helper helper;

    private Event event;

    public DeactivationRecoveryMailTemplate(Helper aHelper, Event aEvent)
    {
        super();
        this.helper = aHelper;
        this.event = aEvent;
    }

    public String getBody()
    {
        String link =
                HelperInteraction.getBaseLink() +
                        "/DeactivationRecoveryReceiver.jsp?helperId=" + helper.getId() + "&eventId=" + event.getId();
        return new HtmlGenerator().withHeader("Hallo " + helper.getFirstName() + "!")
                .withLinebreak()
                .withParagraph(
                        "Leider hast du auf keine unserer Nachfragen reagiert, ob du uns beim Event helfen kannst."
                                + " Nach Ablauf von 4 Wochen wird dein Helfer-Account deshalb deaktiviert. Mit dem Klicken auf den unten stehenden"
                                + " Link kannst du die Deaktivierung verhindern und wirst weiterhin als aktiver Helfer geführt:")
                .withLinebreak()
                .withLink(link, "Deaktivierung verhindern")
                .withLinebreak()
                .withParagraph("Deine Tri-Speedys.")
                .render();
    }

    public String getSubject()
    {
        return "Nachfrage vor Deaktivierung";
    }

    public MessagingFormat getMessagingFormat()
    {
        return MessagingFormat.HTML;
    }

    public MessagingType getMessagingType()
    {
        return MessagingType.DEACTIVATION_REQUEST;
    }
}