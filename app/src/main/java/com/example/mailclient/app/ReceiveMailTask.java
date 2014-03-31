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

public class ReceiveMailTask extends AsyncTask<Object, Object, ArrayList<Email>> {

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
    protected ArrayList<Email> doInBackground(Object... args) {
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
        } catch (Exception e) {
            publishProgress(e.getMessage());
        }

        /*
         *  We don't want a plain Message[] array without content
         *  so let's parse each Message into an ArrayList<Email>
         *  and fill the content (currently only subject and date).
         *
         *  NB: without calling explicitly getSubject() and getDate()
         *  the Message object would be empty.
         */
        ArrayList<Email> list = new ArrayList<Email> ();
        for (int i=0; i < msg.length; i++) {
            Email email = new Email();
            try {
                email.setSubject(msg[i].getSubject());
                email.setDate(msg[i].getSentDate());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            list.add(email);
        }
        return list;
    }

    @Override
    public void onProgressUpdate(Object... values) {
        statusDialog.setMessage(values[0].toString());

    }

    @Override
    public void onPostExecute(ArrayList<Email> result) {

        /*
         *  Notify adapter of the retrieved mails
         *  and store them into cache
         */
        MailClient.adapter.addAll(result);
        MailClient.adapter.notifyDataSetChanged();
        MailClient.save(MailClient.emailList);
        statusDialog.dismiss();
        super.onPostExecute(result);
    }

}