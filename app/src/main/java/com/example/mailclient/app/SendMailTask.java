package com.example.mailclient.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/*
*   Async Task to perform forwarding
*   operation of mails using GMailSender.java class
*   TODO: comment code
*/

public class SendMailTask extends AsyncTask {

    private ProgressDialog statusDialog;
    private Activity sendMailActivity;

    public SendMailTask(Activity activity) {
        this.sendMailActivity = activity;

    }

    protected void onPreExecute() {
        statusDialog = new ProgressDialog(sendMailActivity);
        statusDialog.setMessage("Getting ready...");
        statusDialog.setIndeterminate(false);
        statusDialog.setCancelable(false);
        statusDialog.show();
    }

    /*
    *   Execute task to send email reading from fields
    */
    @Override
    protected Object doInBackground(Object... args) {
        try {
            Log.i("SendMailTask", "About to instantiate GMailSender...");
            publishProgress("Processing input....");

            if (args.length==6){
                GMailSender androidEmail = new GMailSender(args[0].toString(), (List) args[1], args[2].toString(),
                        args[3].toString(), (List) args[4], (List) args[5]);
                publishProgress("Preparing mail message....");
                androidEmail.createEmailMessage();
                publishProgress("Sending email....");
                androidEmail.sendEmail();
                publishProgress("Email Sent.");
                Log.i("SendMailTask", "Mail Sent.");
            }
            else{
                GMailSender androidEmail = new GMailSender(args[0].toString(), (List) args[1], args[2].toString(),
                        args[3].toString(), (List) args[4], (List) args[5], (ArrayList) args[6]);
                publishProgress("Preparing mail message....");
                androidEmail.createEmailMessage();
                publishProgress("Sending email....");
                androidEmail.sendEmail();
                publishProgress("Email Sent.");
                Log.i("SendMailTask", "Mail Sent.");

            }

        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.e("SendMailTask", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
        statusDialog.setMessage(values[0].toString());

    }

    @Override
    public void onPostExecute(Object result) {
        this.sendMailActivity.finish();
        statusDialog.dismiss();
    }

}