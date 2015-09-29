package de.trispeedys.resourceplanning.service;

import java.util.List;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.util.MailSender;

public class MessagingService
{
    @SuppressWarnings("unchecked")
    public static List<MessageQueue> findAllMessages()
    {
        return (List<MessageQueue>) HibernateUtil.fetchResults(MessageQueue.class);
    }

    public static void sendAllMessages()
    {
        for (MessageQueue message : findAllMessages())
        {
            MailSender.sendMail(message.getToAddress(), message.getBody(), message.getSubject());
        }
    }
}