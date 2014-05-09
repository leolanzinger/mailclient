package com.example.mailclient.app;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Leo on 13/04/14.
 */

/*
 *  Here goes all the mailbox logic
 */
public class Mailbox {
    // ArrayList for all received mails
    public static ArrayList<Email> emailList;
    // ArrayList for all sent mails
    public static ArrayList<Email> sentList;
    // SMTP values
    static InternalStorage storer;
    public static Context baseContext;
    public static String account_email = "mailclientandroid@gmail.com";
    public static String account_password = "android2014";
    // internal storage keys
    static String KEY = "mailClient";
    static String SENT_KEY = "mailClient_sent";

    public Mailbox(Context context) {
        baseContext = context;
        storer = new InternalStorage();
        emailList  = new ArrayList<Email>();
        sentList = new ArrayList<Email>();

        try {
            // attempt to read the email list from internal storage
            emailList = (ArrayList<Email>) InternalStorage.readObject(baseContext, KEY);
            //attempt to read sent mail from internal storage
            sentList = (ArrayList<Email>) InternalStorage.readObject(baseContext, SENT_KEY);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * Public method to save email list into
     * internal storage (should be called after every change).
     */
    public static void save(ArrayList<Email> result) {
        try {
            storer.writeObject(baseContext, KEY, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Public method to save sent mail into internal
     * storage. (should be called after sending an email
     * from SendEmailActivity.java).
     */
    public static void saveSent(ArrayList<Email> sent) {
        try {
            storer.writeObject(baseContext, SENT_KEY, sent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
