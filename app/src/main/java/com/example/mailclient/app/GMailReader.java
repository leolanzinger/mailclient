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

    /*
     *  Read only new mails (unread)
     */
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

    /*
     *  Look for mails not stored yet and retrieve it (both read and unread).
     *  Mark unread mails as read
     */
    public synchronized Message[] readLastMails() throws Exception {
        try {
            Folder folder = store.getFolder("Inbox");
            folder.open(Folder.READ_WRITE);
//            int already_count = 1;
//            if ( MailClient.emailList.size() > 0 ) {
//                for (int i = 1; i < folder.getMessageCount(); i++) {
//                    Date cur_date = folder.getMessage(folder.getMessageCount() - i).getSentDate();
//                    if (cur_date == MailClient.emailList.get(0).date) {
//                        already_count = i;
//                    } else {
//                        // set a number of max receivable mails
//                        if (folder.getMessageCount() > 30) {
//                            already_count = 30;
//                        } else {
//                            already_count = folder.getMessageCount();
//                        }
//                    }
//                }
//            }
//            else {
//                if (folder.getMessageCount() > 30) {
//                    already_count = 30;
//                } else {
//                    already_count = folder.getMessageCount();
//                    Log.i("dio cane", "passa prima di qua, numero di messaggi uguale a:");
//                    Log.i("dio cane", String.valueOf(already_count));
//                }
//            }
            Message[] all_msg = folder.getMessages(0, 1);
            folder.setFlags(all_msg, new Flags(Flags.Flag.SEEN), true);
            if (all_msg.length > 0) {
                Log.i("dio cane", "indice maggiore di zero");
            }
            else {
                Log.i("dio cane", "indice uguale a zero");
            }
            return all_msg;
        } catch (Exception e) {
            Log.i("dio cane", "qua ci son problemi");
            return null;
        }
    }
}