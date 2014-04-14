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
    static ArrayList<Email> emailList;
    static InternalStorage storer;
    public static Context baseContext;
    static String KEY = "mailClient";
    public static String account_email = "mailclientandroid@gmail.com";
    public static String account_password = "android2014";

    public Mailbox(Context context) {
        baseContext = context;
        storer = new InternalStorage();
        emailList  = new ArrayList<Email>();

        try {
            ArrayList<Email> emailList = (ArrayList<Email>) InternalStorage.readObject(baseContext, KEY);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void save(ArrayList<Email> result) {
        try {
            storer.writeObject(baseContext, KEY, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
