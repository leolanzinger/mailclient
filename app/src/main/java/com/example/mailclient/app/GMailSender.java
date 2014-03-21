package com.example.mailclient.app;

import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/*
*  GMailSender Java class to send emails using smtp
*  protocol over gmail servers
*/

public class GMailSender {

    final String emailPort = "587";// gmail's smtp port
    final String smtpAuth = "true";
    final String starttls = "true";
    final String emailHost = "smtp.gmail.com";

    String fromEmail;
    String fromPassword;
    List<String> toEmailList;
    String emailSubject;
    String emailBody;
    String fileName;

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;

    public GMailSender(String fromEmail, String fromPassword,
                       List<String> toEmailList, String emailSubject, String emailBody, String fileName) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.fileName =fileName;

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMailSender", "Mail server properties set.");
    }



    public MimeMessage createEmailMessage() throws AddressException,
            MessagingException, IOException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
        for (String toEmail : toEmailList) {
            Log.i("GMailSender","toEmail: "+toEmail);
            emailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));
        }
        emailMessage.setSubject(emailSubject);

        //qua bisogna aggiugnere l'attachment con il percorso "fileName"

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(emailBody, "text/html");

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.attachFile(fileName);

        MimeMultipart multipart = new MimeMultipart("mixed");
        multipart.addBodyPart(textPart);

        emailMessage.setContent(multipart);

       // for a html email
        // emailMessage.setText(emailBody);// for a text email
        Log.i("GMailSender", "Email Message created.");
        return emailMessage;
    }

    public void sendEmail() throws AddressException, MessagingException {

        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromEmail, fromPassword);
        Log.i("GMailSender","allrecipients: "+emailMessage.getAllRecipients());
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        Log.i("GMailSender", "Email sent successfully.");
    }

}