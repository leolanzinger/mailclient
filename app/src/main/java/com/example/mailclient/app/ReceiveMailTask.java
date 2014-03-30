package com.example.mailclient.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import javax.mail.Message;
import javax.mail.MessagingException;

import static android.text.Html.fromHtml;

/*
*   Async Task to perform retrieving
*   operation of mails using GMailSender.java class
*/

public class ReceiveMailTask extends AsyncTask<Object, Object, ArrayList<String>> {

    private ProgressDialog statusDialog;
    private Activity sendMailActivity;

    public ReceiveMailTask(Activity activity) {
        sendMailActivity = activity;

    }

    protected void onPreExecute() {
        statusDialog = new ProgressDialog(sendMailActivity);
        statusDialog.setMessage("Getting ready...");
        statusDialog.setIndeterminate(false);
        statusDialog.setCancelable(false);
        statusDialog.show();
    }

    /*
    *   Execute task to receive email and print logs
    */
    @Override
    protected ArrayList<String> doInBackground(Object... args) {
        Message[] msg = null;
        try {
            Log.i("ReceiveMailTask", "About to instantiate GMailSender...");
            publishProgress("Processing input....");
            GMailReader reader = new GMailReader(args[0].toString(),
                    args[1].toString());
            try {
//                msg = reader.readNewMail();
                msg = reader.readLastMails();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            publishProgress("Email Received.");
//            Log.i("ReceiveMailTask", "Mail Received.");
        } catch (Exception e) {
            publishProgress(e.getMessage());
        }
        ArrayList<String> list = new ArrayList<String> ();
        for (int i=0; i<msg.length; i++) {
            try {
                list.add(msg[i].getSubject());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public void onProgressUpdate(Object... values) {
        statusDialog.setMessage(values[0].toString());

    }

    @Override
    public void onPostExecute(ArrayList<String> result) {

        MainActivity.adapter.addAll(result);
        MainActivity.adapter.notifyDataSetChanged();
        statusDialog.dismiss();
        super.onPostExecute(result);
    }

}