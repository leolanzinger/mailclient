package com.example.mailclient.app;

import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.imap.IMAPStore;

import java.security.Provider;
import java.security.Security;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.search.FlagTerm;

/*
*  GMailSender Java class to receive emails using imap
*  protocol over gmail servers
*/

public class GMailReceiver extends javax.mail.Authenticator {
    private static final String TAG = "GMailReceiver";

    private IMAPStore store;


    private static final Logger logger = Logger.getLogger(GMailReceiver.class.getName());
    private static Session mSession;

    public static final class OAuth2Provider extends Provider {
        private static final long serialVersionUID = 1L;

        public OAuth2Provider() {
            super("Google OAuth2 Provider", 1.0,
                    "Provides the XOAUTH2 SASL Mechanism");
            put("SaslClientFactory.XOAUTH2",
                    "com.example.testjavamail.OAuth2SaslClientFactory");
        }
    }


    public GMailReceiver(String user) {

        this.initialize();

        try {
            store = connectToImap("imap.gmail.com", 993, user, MainActivity.tokenString, true);

            /*
             * Call this method to list all the available folders:
             *
             *   Folder[] folderList = store.getFolder("[Gmail]").list();
             *   for (int i = 0; i < folderList.length; i++) {
             *      Log.i("gmsend", folderList[i].getFullName());
             *   }
             */
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *  Reads only unread mails
     *  and marks them as read
     *  NOT USED ANYMORE
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

    /*
     * Method that reads sent messages from [Gmail]/Posta Inviata
     * IMAP folder. Does not download already stored emails
     */
    public synchronized Message[] readSentMails() throws Exception {
        try {
            Folder folder = store.getFolder("[Gmail]/Posta inviata");
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

    /**
     * Installs the OAuth2 SASL provider. This must be called exactly once before
     * calling other methods on this class.
     */
    public static void initialize() {
        Security.addProvider(new OAuth2Provider());
    }

    /**
     * Connects and authenticates to an IMAP server with OAuth2. You must have
     * called {@code initialize}.
     *
     * @param host Hostname of the imap server, for example {@code
     *     imap.googlemail.com}.
     * @param port Port of the imap server, for example 993.
     * @param userEmail Email address of the user to authenticate, for example
     *     {@code oauth@gmail.com}.
     * @param oauthToken The user's OAuth token.
     * @param debug Whether to enable debug logging on the IMAP connection.
     *
     * @return An authenticated IMAPStore that can be used for IMAP operations.
     */
    public static IMAPStore connectToImap(String host,
                                          int port,
                                          String userEmail,
                                          String oauthToken,
                                          boolean debug) throws Exception {
        Properties props = new Properties();
        props.put("mail.imaps.sasl.enable", "true");
        props.put("mail.imaps.sasl.mechanisms", "XOAUTH2");
//        props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, oauthToken);
        Session session = Session.getInstance(props);
        session.setDebug(debug);

        final URLName unusedUrlName = null;
        IMAPSSLStore store = new IMAPSSLStore(session, unusedUrlName);
        final String emptyPassword = "";
        store.connect(host, port, userEmail, emptyPassword);
        return store;
    }

//TODO: GMailUpdater, ReplyActivity and everything related, to update

}