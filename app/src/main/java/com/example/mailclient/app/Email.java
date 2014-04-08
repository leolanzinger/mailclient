package com.example.mailclient.app;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    boolean seen;
    String subject;
    Date date;
    ArrayList<String> body, body_temp;
    Address[] from,to;
    String excerpt;
    String ID;
    String attachmentPath;

    public Email() {
        subject = new String();
        date = new Date();
        body = new ArrayList<String> ();
        body_temp = new ArrayList<String> ();
        excerpt = "";
        seen = false;
        attachmentPath= "";
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

        Log.i("Check", p.getContentType().toString());
        Multipart mp = (Multipart)p.getContent();
        for (int i=0; i < mp.getCount(); i++) {
            if ( mp.getBodyPart(i).isMimeType("multipart/*")) {
                getContent(mp.getBodyPart(i));
            }
            else {
                if (mp.getBodyPart(i).isMimeType("text/*")) {
                    Log.i("Check", mp.getBodyPart(i).getContentType());
                    String s = (String) mp.getBodyPart(i).getContent();
                    body_temp.add(s);
                }
                else {
                    // TODO: QUI C'è L'ALLEGATO
                    Log.i("Check", mp.getBodyPart(i).getContentType());
                    Log.i("Check", mp.getBodyPart(i).getFileName());


                    saveFile(mp.getBodyPart(i).getFileName(),mp.getBodyPart(i).getInputStream());
                    attachmentPath="media/"+mp.getBodyPart(i).getFileName();

//                    attachmentPath="temp/"+mp.getBodyPart(i).getFileName();
//                    File tempFile = new File(attachmentPath);
//                    InputStream is = mp.getBodyPart(i).getInputStream();
//                    FileOutputStream fos = new FileOutputStream(tempFile);
//                    byte[] buf = new byte[4096];
//                    int bytesRead;
//                    while((bytesRead = is.read(buf))!=-1) {
//                        fos.write(buf, 0, bytesRead);
//                    }
//                    fos.close();
//
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

            //attachment here!
            // disposition == null
            if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) {
                Log.i("excerpt " + i,"Mail have some attachment");
                //buttiamo fuori il multipart!
            }
            else {
                //è testo, quindi lo concateno!
                body_content = body_content.concat(disposition);  // the changed code
                Log.i("excerpt" + i, "body content: " + body_content);

            }
        }

        //butto il body_content in html
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

    public static void saveFile(String filename, InputStream input) throws IOException {
        if (filename == null) {
            filename = File.createTempFile("xx", ".out").getName();
        }
        // Do no overwrite existing file
        File file = new File("media/"+filename);
        for (int i=0; file.exists(); i++) {
            file = new File(filename+i);
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        BufferedInputStream bis = new BufferedInputStream(input);
        int aByte;
        while ((aByte = bis.read()) != -1) {
            bos.write(aByte);
        }
        bos.flush();
        bos.close();
        bis.close();
    }
}
