package com.example.mailclient.app;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.SearchTerm;

/**
 * Created by Leo on 02/04/14.
 */
public class GMailUpdater extends javax.mail.Authenticator {
    private static final String TAG = "GMailUpdater";

    private String mailhost = "imap.gmail.com";
    private Session session;
    private Store store;

    public GMailUpdater(String user, String password) {

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

    /*
     *  Update INBOX folder status:
     *  seen == true -> set it seen
     *  seen == false -> set it unseen
     */
    public synchronized void updateGmail(ArrayList<String> messages, boolean seen) throws Exception {
        try {
            Folder folder = store.getFolder("Inbox");
            folder.open(Folder.READ_WRITE);

            for (String unseen : messages) {
                Message[] uns_msg = folder.search(new MessageIDTerm(unseen));
                if ( seen ) {
                    folder.setFlags(uns_msg, new Flags(Flags.Flag.SEEN), true);
                }
                else {
                    folder.setFlags(uns_msg, new Flags(Flags.Flag.SEEN), false);
                }
            }
        }
        catch (Exception e) {
        }
    }

}