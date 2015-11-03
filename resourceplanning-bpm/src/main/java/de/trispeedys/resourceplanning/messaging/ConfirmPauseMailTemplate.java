package de.trispeedys.resourceplanning.messaging;

import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.util.HtmlGenerator;

public class ConfirmPauseMailTemplate extends AbstractMailTemplate
{
    private Helper helper;

    public ConfirmPauseMailTemplate(Helper aHelper)
    {
        super();
        this.helper = aHelper;
    }

    public String getBody()
    {
        return new HtmlGenerator().withParagraph("Hallo " + helper.getFirstName() + "!")
                .withLinebreak()
                .withParagraph("Schade, dass Du uns dieses Mal nicht helfen kannst. Bis zum nächsten Mal (?)!")
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
        return MessagingType.PAUSE_CONFIRM;
    }
}