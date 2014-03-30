package com.example.mailclient.app;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Leo on 30/03/14.
 */
/*
 *  Java Class that implements an email object to store email contents
 */
public class Email implements Serializable {

    String subject;
    Date date;

    public Email() {
        subject = new String();
        date = new Date();
    }

    public void setSubject(String sub) {
        subject = sub;
    }

    public void setDate(Date dat) {
        date = dat;
    }
}