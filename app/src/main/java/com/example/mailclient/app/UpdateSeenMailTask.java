package com.example.mailclient.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

/*
*   Async task to sync seen messages flag
*/

public class UpdateSeenMailTask extends AsyncTask {

    Activity sendMailActivity;
    boolean password_checked;


    public UpdateSeenMailTask(Activity activity) {
        sendMailActivity = activity;
        password_checked = true;
    }

    protected void onPreExecute() {
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            /*
             *  Updates INBOX status of updated seen/unseen Emails
             */
            Log.i("UpdateSeenMailTask", "About to instantiate GMailUpdater...");
            GMailUpdater updater = new GMailUpdater(Mailbox.account_email, Mailbox.account_password);
            password_checked = updater.passwordChecked();

            try {
                ArrayList<String> seen = new ArrayList<String>();
                seen.add((String) args[0]);
                updater.updateSeenGmail(seen, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
        }

        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
    }

    @Override
    public void onPostExecute(Object result) {
        if(password_checked != true) {
            if (sendMailActivity instanceof MainActivity) ((MainActivity) sendMailActivity).passwordDialog();
            else if(sendMailActivity instanceof ReadMail) ((ReadMail) sendMailActivity).passwordDialog();
            Log.d("Check", "UpdateSeenMailTask");
        }
    }

}