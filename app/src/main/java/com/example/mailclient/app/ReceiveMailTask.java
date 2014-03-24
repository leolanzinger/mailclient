package com.example.mailclient.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import javax.mail.Message;

/*
*   Async Task to perform retrieving
*   operation of mails using GMailSender.java class
*/

public class ReceiveMailTask extends AsyncTask <Object, Object, Message>{

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
    protected Message doInBackground(Object... args) {
        Message msg_ret = null;
        try {
            Log.i("ReceiveMailTask", "About to instantiate GMailSender...");
            publishProgress("Processing input....");
            GMailReader reader = new GMailReader(args[0].toString(),
                    args[1].toString());
            try {
                Message[] msg = reader.readMail();
//                msg.length - 1 is the index for last received email
                Log.i("read", msg[msg.length - 1].getSubject());
                msg_ret = msg[msg.length - 1];
            } catch (Exception e) {
                e.printStackTrace();
            }
            publishProgress("Email Received.");
            Log.i("ReceiveMailTask", "Mail Received.");
        } catch (Exception e) {
            publishProgress(e.getMessage());
            Log.e("ReceiveMailTask", e.getMessage(), e);
        }
        return msg_ret;
    }

    @Override
    public void onProgressUpdate(Object... values) {
        statusDialog.setMessage(values[0].toString());

    }

    @Override
    public void onPostExecute(Message result) {
        statusDialog.dismiss();
        super.onPostExecute(result);
    }

}