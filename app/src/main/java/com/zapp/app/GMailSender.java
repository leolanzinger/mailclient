package com.zapp.app;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
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
*  TODO: comment code
*/

public class GMailSender {

    private boolean isPasswordCorrect;

    final String emailPort = "587";// gmail's smtp port
    final String smtpAuth = "true";
    final String starttls = "true";
    final String emailHost = "smtp.gmail.com";

    String fromEmail;
    String fromPassword;
    List<String> toEmailList, ccEmailList, bccEmailList;
    String emailSubject;
    String emailBody;
    ArrayList<String> fileName = new ArrayList<String>();

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;

    public GMailSender(String fromEmail, String fromPassword, List<String> toEmailList, String emailSubject, String emailBody, List<String> ccEmailList, List<String> bccEmailList) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList;
        this.ccEmailList = ccEmailList;
        this.bccEmailList = bccEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMailSender", "Mail server properties set.");

        isPasswordCorrect = true;

    }

    public GMailSender(String fromEmail, String fromPassword, List<String> toEmailList, String emailSubject, String emailBody, List<String> ccEmailList, List<String> bccEmailList, ArrayList<String> fileName) {

        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList;
        this.ccEmailList = ccEmailList;
        this.bccEmailList = bccEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.fileName =fileName;

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMailSender", "Mail server properties set.");

        isPasswordCorrect = true;

    }

    public MimeMessage createEmailMessage() throws AddressException, MessagingException, IOException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
        for (String toEmail : toEmailList) {
            Log.i("GMailSender","toEmail: "+toEmail);
            emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        }
        if (ccEmailList.get(0)!="") {
            for (String toEmail : ccEmailList) {
                Log.i("GMailSender","ccEmail: "+toEmail);
                emailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(toEmail));
            }
        }
        if (bccEmailList.get(0)!=""){
            for (String toEmail : bccEmailList) {
                Log.i("GMailSender","bccEmail: "+toEmail);
                emailMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(toEmail));
            }
        }

        emailMessage.setSubject(emailSubject);

        // create the message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        //fill message and add it
        messageBodyPart.setText(emailBody);
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment, check if there is one, and handle the multiple ones

        if (fileName.isEmpty()) {
        }
        else{
            for (int i = 0; i < fileName.size(); i++) {
                messageBodyPart = new MimeBodyPart();
                Log.i("ImageSent","Immagini in invio: "+ fileName.get(i));
                DataSource source = new FileDataSource(fileName.get(i));
                messageBodyPart.setDataHandler(new DataHandler(source));
                String name = new File(fileName.get(i)).getName();
                messageBodyPart.setFileName(name);
                multipart.addBodyPart(messageBodyPart);
            }
        }

        // Put parts in message
        emailMessage.setContent(multipart);
        Log.i("GMailSender", "Email Message created.");
        return emailMessage;
    }

    public void sendEmail() throws AddressException, MessagingException {

        Transport transport = mailSession.getTransport("smtp");

        try {
            transport.connect(emailHost, fromEmail, fromPassword);
            Log.i("GMailSender","allrecipients: "+emailMessage.getAllRecipients());
            transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        } catch (AuthenticationFailedException e){
            transport.close();
            isPasswordCorrect = false;
            e.printStackTrace();
        }


        transport.close();
        Log.i("GMailSender", "Email sent successfully.");
    }

    public boolean passwordChecked() {
        return isPasswordCorrect;
    }

}