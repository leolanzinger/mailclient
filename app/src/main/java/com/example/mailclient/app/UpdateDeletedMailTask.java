package com.example.mailclient.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

/*
*   Async task to sync messages flags - not used yet
*/

public class UpdateDeletedMailTask extends AsyncTask {

    private Activity sendMailActivity;

    public UpdateDeletedMailTask(Activity activity) {
        sendMailActivity = activity;

    }

    protected void onPreExecute() {
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            /*
             *  Updates INBOX status of updated Emails
             */
            Log.i("UpdateSeenMailTask", "About to instantiate GMailUpdater...");
            GMailUpdater updater = new GMailUpdater(Mailbox.account_email, Mailbox.account_password);
            try {
                ArrayList<String> deleted = new ArrayList<String>();
                deleted.add((String) args[0]);
                updater.updateDeletedGmail(deleted, true);
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
    }

}