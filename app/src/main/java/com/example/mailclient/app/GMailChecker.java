package com.example.mailclient.app;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Created by teo on 10/06/14.
 */
public class GMailChecker {

    private static final String TAG = "GMailChecker";

    private String mailhost = "imap.gmail.com";
    private Session session;
    private Store store;
    private boolean isConnected;


    public GMailChecker(String user, String password) {

        /*
         * Set SMTP variables
         */
        isConnected = true;
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
        } catch (MessagingException e) {
            isConnected = false;
            e.printStackTrace();
        }
    }

    public boolean connected() {
        return isConnected;
    }
}
