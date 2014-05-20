package com.example.mailclient.app;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Leo on 20/05/14.
 */
public class SpeechAnalyzer {

    public void analyze(ArrayList<String> result, Activity activity, Email email) {
        ArrayList<String> myList = new ArrayList<String>(Arrays.asList(result.get(0).split("\\s+")));
        if (myList.get(0).equals("rispondi")) {
            if (myList.get(1).equals("tutti")){
                myList.remove(0);
                myList.remove(1);
                String reply = myList.toString();
                ((ReadMail) activity).replyAll(reply);
            }
            else {
                myList.remove(0);
                String reply = myList.toString();
                ((ReadMail) activity).replyMail(reply);
            }
        }
        else if (myList.get(0).equals("ricordamelo")) {
            if (myList.get(1).equals("oggi")){
                GregorianCalendar today_date = new GregorianCalendar();
                // Add email to todolist
                email.addTodo(today_date);
                //set toast
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(activity, "Reminder set for today", duration);
                toast.show();
            }
            else if (myList.get(1).equals("domani")) {
                GregorianCalendar tomorrow_date = new GregorianCalendar();
                tomorrow_date.add(Calendar.DATE, 1);
                email.addTodo(tomorrow_date);
                //set toast
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(activity, "Reminder set for tomorrow", duration);
                toast.show();
            }
            else {
                email.addTodo(null);
                //set toast
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(activity, "Reminder set without date", duration);
                toast.show();
            }
            activity.findViewById(R.id.readMail_pin).setBackgroundColor(R.color.yellow);
            activity.findViewById(R.id.readMail_pin).setBackgroundResource(R.drawable.pinned);
        }
    }
}
