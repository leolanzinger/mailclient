package com.example.mailclient.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;

/**
 * Created by Leo on 30/03/14.
 */
/*
 *  Java Class that implements an email object to store email contents
 */
public class Email implements Serializable {

    /*
     * Email variables
     */
    boolean todo, seen, deleted;
    String subject, excerpt, ID, ID_START, ID_END;
    Date date;
    ArrayList<String> body, body_temp, attachmentPath;
    Address[] from,to,cc;
    GregorianCalendar expire_date;


    /*
     *  Initialize variables when istantiating
     *  new Email object
     */
    public Email() {
        subject = new String();
        date = new Date();
        body = new ArrayList<String> ();
        body_temp = new ArrayList<String> ();
        excerpt = "";
        seen = false;
        attachmentPath= new ArrayList<String>();
        todo = false;
        deleted = false;
        expire_date = null;
    }

    /*
     *  Email filler methods
     */

    public void setSeen() {
        seen = true;
    }

    public void setUnSeen() {
        seen = false;
    }

    public void setSubject(String sub) {
        subject = sub;
    }

    public void setDate(Date dat) {
        date = dat;
    }

    public void setFrom(Address[] addr) {
        if (addr != null) {
            from = new Address[addr.length];
            for (int i = 0; i < addr.length; i++) {
                from[i] = addr[i];
            }
        }
    }

    public void setTo(Address[] addr) {
        if (addr != null) {
            to = new Address[addr.length];
            for (int i = 0; i < addr.length; i++) {
                to[i] = addr[i];
            }
        }
    }

    public void setCC(Address[] addr) {
        if (addr != null) {
            cc = new Address[addr.length];
            for (int i = 0; i < addr.length; i++) {
                cc[i] = addr[i];
            }
            // if to is null set to as cc so the app won't crash
            if (to == null) {
                to = new Address[cc.length];
                for (int i = 0; i < cc.length; i++) {
                    to[i] = cc[i];
                }
            }
        }
    }

    /*
     * This is a hack to avoid empty cc emails
     */
    public void setNoCC() {
        cc = new Address[0];
    }

    /*
     * Hack to avoid empty to emails
     */
    public void setEmptyRecipient(Address[] addr_bcc) {
        if (to == null || to.length == 0){
            if (cc == null || cc.length == 0) {
                Log.d("to null", "set to as bcc");
                to = new Address[addr_bcc.length];
                for (int i = 0; i < addr_bcc.length; i++) {
                    to[i] = addr_bcc[i];
                }
            }
        }
    }

    /*
     *  Set body content
     */
    public void setContent(Message msg) throws MessagingException, IOException {
        getContent(msg);
        if (body_temp != null && body_temp.size() != 0) {
            body.add(body_temp.get(body_temp.size() - 1));
        }
        setExcerpt();
    }

    /*
     * Return the primary content of the message.
     */
    private void getContent(Part p) throws MessagingException, IOException {
        if(p.isMimeType("text/*")){
            String s = (String) p.getContent();
            body_temp.add(s);
        }
        else {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                if (mp.getBodyPart(i).isMimeType("multipart/*")) {
                    getContent(mp.getBodyPart(i));
                } else {
                    if (mp.getBodyPart(i).isMimeType("text/*")) {
                        String s = (String) mp.getBodyPart(i).getContent();
                        body_temp.add(s);
                    } else {
                        String filePath = "storage/sdcard0/Download/" + mp.getBodyPart(i).getFileName();
                        saveFile(mp.getBodyPart(i).getInputStream(), filePath);
                        attachmentPath.add(filePath);
                    }
                }
            }
        }
    }

    /*
     *  Parse body content and extract the excerpt of body.
     *  - saves only first line without multiple spaces
     */
    public void setExcerpt() throws MessagingException, IOException {

        //TODO: pulirlo

        String body_content = "";
        for (int i=0; i<body.size(); i++) {
            String disposition = body.get(i);
            body_content = body_content.concat(disposition);
        }

        Spanned spanned = Html.fromHtml(body_content);
        if (spanned.toString().replaceAll("  ","").split("\\r?\\n")[0].length() > 39) {
            excerpt += spanned.toString().replaceAll("  ", "").split("\\r?\\n")[0].substring(0, 39);
        }
        else {
            excerpt += spanned.toString().replaceAll("  ", "").split("\\r?\\n")[0];
        }
        excerpt += "...";
    }

    /*
     *  Set ID for every message (so we can check online unread / read messages)
     */
    public void setID(String s) {
        ID = s;
        ID_START = s.concat(String.valueOf(Mailbox.scheduler_start.getTimeInMillis()));
        ID_END = s.concat(String.valueOf(Mailbox.scheduler_end.getTimeInMillis()));
    }

    /*
     *  Save method to store attachment
     *  to internal storage
     */
    public void saveFile(InputStream uploadedInputStream, String serverLocation) {
        try {
            OutputStream outputStream;
            int read = 0;
            byte[] bytes = new byte[1024];
            outputStream = new FileOutputStream(new File(serverLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Public methods to set / unset
     * Email object as TodoEmail Object
     */
    public void addTodo(GregorianCalendar date) {
        setReminder(date);
        todo = true;
    }

    public void removeTodo() {
        if (expire_date != null) {
            /*
             *  Cancel alarms
             */
            Intent intent_start = new Intent(Mailbox.baseContext, AlarmReceiver.class);
            PendingIntent int_start = PendingIntent.getBroadcast(Mailbox.baseContext, ID_START.hashCode(), intent_start, 0);
            Intent intent_end = new Intent(Mailbox.baseContext, AlarmReceiver.class);
            PendingIntent int_end = PendingIntent.getBroadcast(Mailbox.baseContext, ID_END.hashCode(), intent_end, 0);
            Mailbox.alManager.cancel(int_start);
            Mailbox.alManager.cancel(int_end);
        }
        expire_date = null;
        todo = false;
    }

    public void setReminder(GregorianCalendar date) {
        expire_date = date;
        if (date != null) {
            /*
             *  set start alarm
             */
            Calendar cal = Calendar.getInstance();
            if (date.after(cal) ) {
                /*
                 * set start alarm
                 */
                Intent intent = new Intent(Mailbox.baseContext, AlarmReceiver.class);
                String notification_msg;
                if (subject == null) {
                    notification_msg = "<nessun oggetto>";
                }
                else {
                    notification_msg = subject;
                }
                intent.putExtra("TITLE", "Today's task:");
                intent.putExtra("EMAIL", notification_msg);
                intent.putExtra("ID", ID.hashCode());
                GregorianCalendar alarm_start_cal = date;
                alarm_start_cal.set(GregorianCalendar.HOUR_OF_DAY, Mailbox.scheduler_start.get(GregorianCalendar.HOUR_OF_DAY));
                alarm_start_cal.set(GregorianCalendar.MINUTE, Mailbox.scheduler_start.get(GregorianCalendar.MINUTE));
                alarm_start_cal.set(GregorianCalendar.SECOND, 0);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(Mailbox.baseContext, ID_END.hashCode() , intent, 0);
                Mailbox.alManager.set(AlarmManager.RTC, alarm_start_cal.getTimeInMillis(), pendingIntent);
                Mailbox.intentArray.add(pendingIntent);
                Log.i("alarm", "alarm set for " + "Oggi devi fare: \"" + notification_msg + "\"");
                Log.i("alarm", "alarm set at " + alarm_start_cal.getTime());
            }

            /*
             * set end alarm
             */
            Intent intent = new Intent(Mailbox.baseContext, AlarmReceiver.class);
            String notification_msg;
            if (subject == null) {
                notification_msg = "<nessun oggetto>";
            }
            else {
                notification_msg = subject;
            }
            intent.putExtra("TITLE", "Uncompleted task:");
            intent.putExtra("EMAIL", notification_msg);
            intent.putExtra("ID", ID.hashCode());
            GregorianCalendar alarm_end_cal = date;
            alarm_end_cal.set(GregorianCalendar.HOUR_OF_DAY, Mailbox.scheduler_end.get(GregorianCalendar.HOUR_OF_DAY));
            alarm_end_cal.set(GregorianCalendar.MINUTE, Mailbox.scheduler_end.get(GregorianCalendar.MINUTE));
            alarm_end_cal.set(GregorianCalendar.SECOND, 0);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(Mailbox.baseContext, ID_END.hashCode() , intent, 0);
            Mailbox.alManager.set(AlarmManager.RTC, alarm_end_cal.getTimeInMillis(), pendingIntent);
            Mailbox.intentArray.add(pendingIntent);
            Log.i("alarm", "alarm set for " + "Oggi non hai fatto: \"" + notification_msg + "\"");
            Log.i("alarm", "alarm set at " + alarm_end_cal.getTime());
        }
    }
}
