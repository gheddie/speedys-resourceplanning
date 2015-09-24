package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.logic.MessagingHelper;

public class MailingTest
{
    @Test
    public void testSendMails()
    {
        MessagingHelper.sendAllMessages();
    }
}