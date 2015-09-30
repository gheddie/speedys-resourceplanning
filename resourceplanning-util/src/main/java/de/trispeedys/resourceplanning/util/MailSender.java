package de.trispeedys.resourceplanning.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender
{
    private static final String FROM = "from-email@gmail.com";

    public static void sendMail(String toAddress, String body, String subject)
    {
        final String username = "testhelper1.trispeedys@gmail.com";
        final String password = "trispeedys1234";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(username, password);
            }
        });
        try
        {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("Done");
        }
        catch (MessagingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void sendHtmlMail(Long helperId, Long eventId)
    {
        final String username = "testhelper1.trispeedys@gmail.com";
        final String password = "trispeedys1234";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(username, password);
            }
        });
        try
        {
            Message msg = new MimeMessage(session);
            msg.setSubject("Test Notification");
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress("testhelper1.trispeedys@gmail.com", false));
            StringBuffer buffer = new StringBuffer();
            buffer.append("<a href=\"http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/HelperCallbackReceiver.jsp?callbackResult=ASSIGNMENT_AS_BEFORE&helperId="+helperId+"&eventId="+eventId+"\">Wie immer</a>");
            buffer.append("<br>");
            buffer.append("<a href=\"http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/HelperCallbackReceiver.jsp?callbackResult=PAUSE_ME&helperId="+helperId+"&eventId="+eventId+"\">Diesmal nicht helfen</a>");
            buffer.append("<br>");
            buffer.append("<a href=\"http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/HelperCallbackReceiver.jsp?callbackResult=CHANGE_POS&helperId="+helperId+"&eventId="+eventId+"\">Auf anderer Position helfen</a>");
            msg.setContent(buffer.toString(), "text/html; charset=utf-8");
            msg.setSentDate(new Date());
            Transport.send(msg);
        }
        catch (MessagingException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    //---
    
    public static void main(String[] args)
    {
        Long helperId = new Long(3084);
        Long eventId = new Long(3083);
        sendHtmlMail(helperId, eventId);
    }
}