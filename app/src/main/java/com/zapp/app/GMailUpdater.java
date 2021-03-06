package com.zapp.app;


import java.util.ArrayList;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.MessageIDTerm;

/**
 * Created by Leo on 02/04/14.
 */

/*
*  GMailUpdater Java class to notify email state change using smtp
*  protocol over gmail servers
*/

public class GMailUpdater extends javax.mail.Authenticator {
    private static final String TAG = "GMailUpdater";

    private String mailhost = "imap.gmail.com";
    private Session session;
    private Store store;
    private boolean isPasswordCorrect;


    public GMailUpdater(String user, String password) {

        /*
         * Set SMTP variables
         */

        isPasswordCorrect = true;
        Properties props = System.getProperties();
        if (props == null){
        }else{
            props.setProperty("mail.store.protocol", "imaps");
        }
        try {
            session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect(mailhost, user, password);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (AuthenticationFailedException e){
            //Here it is the exception for password wrong
            isPasswordCorrect = false;
        } catch (MessagingException e) {
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

    public boolean passwordChecked() {
        return isPasswordCorrect;
    }


}