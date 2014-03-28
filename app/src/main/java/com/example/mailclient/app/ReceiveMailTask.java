package com.example.mailclient.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.Spanned;
import android.util.Log;

import java.util.List;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.Multipart;

import static android.text.Html.fromHtml;

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
                Message[] msg = reader.readNewMail();
                for (int i=0; i < msg.length; i++) {
                    String msg_subject = msg[i].getSubject();
                    Log.i("new mail", msg_subject);
                    Multipart multipart = (Multipart) msg[i].getContent();
                    String msg_content = new String();
                    for (int j = 0; j < multipart.getCount(); j++) {
                        BodyPart bodyPart = multipart.getBodyPart(j);
                        String disposition = bodyPart.getDisposition();
                        if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) { // BodyPart.ATTACHMENT doesn't work for gmail
                            DataHandler handler = bodyPart.getDataHandler();
                        } else {
                            msg_content = bodyPart.getContent().toString();
                        }
                    }
                    Log.i("new mail", msg_content);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            publishProgress("Email Received.");
            Log.i("ReceiveMailTask", "Mail Received.");
        } catch (Exception e) {
            publishProgress(e.getMessage());
//            Log.e("ReceiveMailTask", e.getMessage(), e);
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