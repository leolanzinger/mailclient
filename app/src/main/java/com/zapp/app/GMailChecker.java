package com.zapp.app;

import java.util.Properties;

import javax.mail.AuthenticationFailedException;
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
    private boolean isPasswordCorrect;


    public GMailChecker(String user, String password) {

        /*
         * Set SMTP variables
         */
        isConnected = true;
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
            isConnected = false;
            e.printStackTrace();
        }
    }

    public boolean passwordChecked() {
        return isPasswordCorrect;
    }

    public boolean connected() {
        return isConnected;
    }
}
