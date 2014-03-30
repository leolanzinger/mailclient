package com.example.mailclient.app;

import android.util.Log;

import java.util.*;
import javax.mail.*;
import javax.mail.search.FlagTerm;

/*
*  GMailSender Java class to receive emails using imap
*  protocol over gmail servers
*/

public class GMailReader extends javax.mail.Authenticator {
    private static final String TAG = "GMailReader";

    private String mailhost = "imap.gmail.com";
    private Session session;
    private Store store;

    public GMailReader(String user, String password) {

        Properties props = System.getProperties();
        if (props == null){
            Log.e(TAG, "Properties are null !!");
        }else{
            props.setProperty("mail.store.protocol", "imaps");

            Log.d(TAG, "Transport: "+props.getProperty("mail.transport.protocol"));
            Log.d(TAG, "Store: "+props.getProperty("mail.store.protocol"));
            Log.d(TAG, "Host: "+props.getProperty("mail.imap.host"));
            Log.d(TAG, "Authentication: "+props.getProperty("mail.imap.auth"));
            Log.d(TAG, "Port: "+props.getProperty("mail.imap.port"));
        }
        try {
            session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect(mailhost, user, password);
            Log.i(TAG, "Store: "+store.toString());
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // reads new mails
    public synchronized Message[] readNewMail() throws Exception {
        try {
            Folder folder = store.getFolder("Inbox");
            folder.open(Folder.READ_WRITE);
            Message[] new_msgs = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            folder.setFlags(new_msgs, new Flags(Flags.Flag.SEEN), true);
            return new_msgs;
        } catch (Exception e) {
            return null;
        }
    }

    // reads last 20 mails
    public synchronized Message[] readLastMails() throws Exception {
        try {
            Folder folder = store.getFolder("Inbox");
            folder.open(Folder.READ_WRITE);
            Message[] all_msgs = folder.getMessages(folder.getMessageCount() - 21 , folder.getMessageCount() - 1);
            return all_msgs;
        } catch (Exception e) {
            return null;
        }
    }
}