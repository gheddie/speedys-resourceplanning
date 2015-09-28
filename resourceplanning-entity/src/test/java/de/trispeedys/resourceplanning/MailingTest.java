package de.trispeedys.resourceplanning;

import org.junit.Test;

import de.trispeedys.resourceplanning.service.MessagingService;

public class MailingTest
{
    @Test
    public void testSendMails()
    {
        MessagingService.sendAllMessages();
    }
}