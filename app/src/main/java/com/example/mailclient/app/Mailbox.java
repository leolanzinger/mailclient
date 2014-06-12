package com.example.mailclient.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
    public static String account_email;
    public static String account_password;
    public static String account_name;
    public static String account_id;
    // internal storage keys
    static String KEY = "mailClient";
    static String SENT_KEY = "mailClient_sent";
    // reminder calendars
    public static GregorianCalendar scheduler_start, scheduler_end;
    // alarmList
    public static ArrayList<PendingIntent> intentArray;
    // alarm manager
    public static AlarmManager alManager;
    // avoid null pointer on read mail when onResume()
    public static int last_mail_read_index;

    public Mailbox(Context context) {
        baseContext = context;
        storer = new InternalStorage();
        emailList  = new ArrayList<Email>();
        sentList = new ArrayList<Email>();
        intentArray =  new ArrayList<PendingIntent>();

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

        /*
         *  Let's hardcode alarm times
         */
        scheduler_start = new GregorianCalendar();
        scheduler_end = new GregorianCalendar();
        scheduler_start.set(Calendar.HOUR_OF_DAY, 8);
        scheduler_start.set(Calendar.MINUTE, 0);
        scheduler_end.set(Calendar.HOUR_OF_DAY, 18);
        scheduler_end.set(Calendar.MINUTE, 0);

        /*
         *  Istantiate alarm manager
         */
        alManager = (AlarmManager) baseContext.getSystemService(baseContext.ALARM_SERVICE);

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
