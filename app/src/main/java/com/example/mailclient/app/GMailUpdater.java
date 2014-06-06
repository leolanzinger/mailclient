package com.example.mailclient.app;


import com.sun.mail.imap.IMAPStore;

import java.util.ArrayList;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.search.MessageIDTerm;

/**
 * Created by Leo on 02/04/14.
 */

/*
*  GMailUpdater Java class to notify email state change using smtp
*  protocol over gmail servers
*/

public class GMailUpdater extends javax.mail.Authenticator {
    private IMAPStore store;

    public GMailUpdater(String user) {

        /*
         * Set SMTP variables
         */

        GMailReceiver.initialize();
        try {
            store = GMailReceiver.connectToImap("imap.gmail.com", 993, user, MainActivity.tokenString, true);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *  Update INBOX folder status for mail seen flag:
     *  seen == true -> set it seen
     *  seen == false -> set it unseen
     */
    public synchronized void updateSeenGmail(ArrayList<String> messages, boolean seen) throws Exception {
        try {
            Folder folder = store.getFolder("Inbox");
            folder.open(Folder.READ_WRITE);

            for (String unseen : messages) {
                Message[] uns_msg = folder.search(new MessageIDTerm(unseen));
                folder.setFlags(uns_msg, new Flags(Flags.Flag.SEEN), seen);
            }
        }
        catch (Exception e) {
        }
    }

    /*
     *  Update INBOX folder status for mail deleted flag:
     *  deleted == true -> set it deleted
     *  deleted == false -> set it not deleted
     */
    public synchronized void updateDeletedGmail(ArrayList<String> messages, boolean deleted) throws Exception {
        try {
            Folder folder = store.getFolder("Inbox");
            folder.open(Folder.READ_WRITE);

            for (String unseen : messages) {
                Message[] uns_msg = folder.search(new MessageIDTerm(unseen));
                folder.setFlags(uns_msg, new Flags(Flags.Flag.DELETED), deleted);
            }
        }
        catch (Exception e) {
        }
    }

}