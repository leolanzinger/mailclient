package com.example.mailclient.app;

import android.util.Log;

import java.util.*;
import javax.mail.*;
import javax.mail.search.FlagTerm;

/*
*  GMailSender Java class to receive emails using imap
*  protocol over gmail servers
*/

public class GMailReceiver extends javax.mail.Authenticator {
    private static final String TAG = "GMailReceiver";

    private String mailhost = "imap.gmail.com";
    private Session session;
    private Store store;

    public GMailReceiver(String user, String password) {

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
     *  Reads only unread mails
     *  and marks them as read
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
     *  Reads mails from the inbox that are not cached yet
     *  Marks every downloaded email as read in the IMAP folder
     *  It retrieves a maximum of 30 mails.
     */
    public synchronized Message[] readLastMails() throws Exception {
        try {
            Folder folder = store.getFolder("Inbox");
            folder.open(Folder.READ_WRITE);
            int already_count = 1;
            int mess_count = folder.getMessageCount();
            if ( Mailbox.emailList.size() > 0 ) {
                for (int i = 0; i < mess_count; i++) {
                    String cur_ID = folder.getMessage(mess_count - i).getHeader("Message-Id")[0];
                    if ( cur_ID.equals(Mailbox.emailList.get(0).ID) ) {
                        already_count = i;
                        i = mess_count + 1;
                    } else {
                        // set a number of max receivable mails
                        if (mess_count > 30) {
                            already_count = 30;
                        } else {
                            already_count = mess_count;
                        }
                    }

                }
            }
            else {
                Log.i("GmailReceiver", "casella mail read vuota");
                if (mess_count > 30) {
                    already_count = 30;
                } else {
                    already_count = mess_count;
                }
            }
            if ( (mess_count - already_count) != mess_count ) {
                return folder.getMessages( (mess_count - already_count) + 1, mess_count);
            }
            else {
                return new Message[0];
            }
        } catch (Exception e) {
            Log.i("GmailReceiver", "exception");
            return null;
        }
    }

    // TODO: implement receivedSentMail() and add the result list to ReceiveInboxTask and add emails to Mailbox.sentList
    public synchronized Message[] readSentMails() throws Exception {
        try {
            Folder folder = store.getFolder("[Gmail]/Sent Mail");
            folder.open(Folder.READ_WRITE);
            int already_count = 1;
            int mess_count = folder.getMessageCount();
            if ( Mailbox.sentList.size() > 0 ) {
                for (int i = 0; i < mess_count; i++) {
                    String cur_ID = folder.getMessage(mess_count - i).getHeader("Message-Id")[0];
                    if ( cur_ID.equals(Mailbox.sentList.get(0).ID) ) {
                        already_count = i;
                        i = mess_count + 1;
                    } else {
                        // set a number of max receivable mails
                        if (mess_count > 30) {
                            already_count = 30;
                        } else {
                            already_count = mess_count;
                        }
                    }

                }
            }
            else {
                Log.i("GmailReceiver", "casella mail sent vuota");
                if (mess_count > 30) {
                    already_count = 30;
                } else {
                    already_count = mess_count;
                }
            }
            if ( (mess_count - already_count) != mess_count ) {
                return folder.getMessages( (mess_count - already_count) + 1, mess_count);
            }
            else {
                return new Message[0];
            }

        } catch (Exception e) {
            return null;
        }
    }
}