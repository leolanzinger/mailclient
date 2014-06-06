package com.example.mailclient.app;

import android.util.Log;

import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.URLName;
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

    final String emailPort = "587";// gmail's smtp port
    final String smtpAuth = "true";
    final String starttls = "true";

    String fromEmail;
    List<String> toEmailList, ccEmailList, bccEmailList;
    String emailSubject;
    String emailBody;
    ArrayList<String> fileName = new ArrayList<String>();

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;

    public GMailSender(String fromEmail, List<String> toEmailList, String emailSubject, String emailBody, List<String> ccEmailList, List<String> bccEmailList) {
        this.fromEmail = fromEmail;
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
    }

    public GMailSender(String fromEmail, List<String> toEmailList, String emailSubject, String emailBody, List<String> ccEmailList, List<String> bccEmailList, ArrayList<String> fileName) {

        this.fromEmail = fromEmail;
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

    public void sendEmail() throws Exception {

        /*
         * Trying new version
         */

        SMTPTransport smtpTransport = connectToSmtp("smtp.gmail.com",587,fromEmail,MainActivity.tokenString,true);

        Log.i("GMailSender","allrecipients: "+emailMessage.getAllRecipients());
        smtpTransport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        smtpTransport.close();
        Log.i("GMailSender", "Email sent successfully.");
    }

    public SMTPTransport connectToSmtp(String host, int port, String userEmail,String oauthToken, boolean debug) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.sasl.enable", "false");
        props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, oauthToken);
        mailSession = Session.getInstance(props);
        mailSession.setDebug(debug);

        final URLName unusedUrlName = null;
        SMTPTransport transport = new SMTPTransport(mailSession, unusedUrlName);
        // If the password is non-null, SMTP tries to do AUTH LOGIN.
        final String emptyPassword = null;
        transport.connect(host, port, userEmail, emptyPassword);

        byte[] response = String.format("user=%s\1auth=Bearer %s\1\1", userEmail,oauthToken).getBytes();
        response = BASE64EncoderStream.encode(response);

        transport.issueCommand("AUTH XOAUTH2 " + new String(response), 235);

        return transport;
    }

}