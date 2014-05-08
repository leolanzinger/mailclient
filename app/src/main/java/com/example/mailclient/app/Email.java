package com.example.mailclient.app;

import android.text.Html;
import android.text.Spanned;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

/**
 * Created by Leo on 30/03/14.
 */
/*
 *  Java Class that implements an email object to store email contents
 */
public class Email implements Serializable {

    boolean todo;
    boolean seen;
    String subject;
    Date date;
    ArrayList<String> body, body_temp, attachmentPath;
    Address[] from,to,cc;
    String excerpt;
    String ID;

    public Email() {
        subject = new String();
        date = new Date();
        body = new ArrayList<String> ();
        body_temp = new ArrayList<String> ();
        excerpt = "";
        seen = false;
        attachmentPath= new ArrayList<String>();
        todo = false;
    }

    /*
     *  Fill up Email object
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
        from = new Address[addr.length];
        for (int i = 0; i < addr.length; i++) {
            from[i] = addr[i];
        }
    }

    public void setTo(Address[] addr) {
        to = new Address[addr.length];
        for (int i = 0; i < addr.length; i++) {
            to[i] = addr[i];
        }
    }
    public void setCC(Address[] addr) {
        cc = new Address[addr.length];
        for (int i = 0; i < addr.length; i++) {
            cc[i] = addr[i];
        }
    }

    /*
     *  Store body content
     */
    public void setContent(Message msg) throws MessagingException, IOException {
        // TODO: call getContent sul message
        getContent(msg);
        if (body_temp != null && body_temp.size() != 0) {
            body.add(body_temp.get(body_temp.size() - 1));
        }
        // TODO: FINALLY CALL setExcerpt()
        setExcerpt();
    }

    /**
     * Return the primary content of the message.
     */
    private void getContent(Part p) throws MessagingException, IOException {

        Multipart mp = (Multipart)p.getContent();
        for (int i=0; i < mp.getCount(); i++) {
            if ( mp.getBodyPart(i).isMimeType("multipart/*")) {
                getContent(mp.getBodyPart(i));
            }
            else {
                if (mp.getBodyPart(i).isMimeType("text/*")) {
                    String s = (String) mp.getBodyPart(i).getContent();
                    body_temp.add(s);
                }
                else {
                    String filePath= "storage/sdcard0/Download/"+mp.getBodyPart(i).getFileName();
                    saveFile(mp.getBodyPart(i).getInputStream(), filePath);
                    attachmentPath.add(filePath);
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
    }

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

    public void addTodo() {
        todo = true;
    }

    public void removeTodo() {
        todo = false;
    }
}
