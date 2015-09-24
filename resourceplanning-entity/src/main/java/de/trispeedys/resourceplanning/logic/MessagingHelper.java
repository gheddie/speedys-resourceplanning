package de.trispeedys.resourceplanning.logic;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.util.MailSender;

public class MessagingHelper
{
    @SuppressWarnings("unchecked")
    public static List<MessageQueue> findAllMessages()
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query q =
                session.createQuery("From " +
                        MessageQueue.class.getSimpleName() + " mq");
        List<MessageQueue> messages = q.list();
        session.close();
        return messages;
    }

    public static void sendAllMessages()
    {
        for (MessageQueue message : findAllMessages())
        {
            MailSender.sendMail(message.getToAddress(), message.getBody(), message.getSubject());
        }
    }
}