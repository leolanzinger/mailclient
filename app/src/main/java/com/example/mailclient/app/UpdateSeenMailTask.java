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

    public UpdateSeenMailTask(Activity activity) {
        sendMailActivity = activity;

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
            GMailUpdater updater = new GMailUpdater(Mailbox.account_email);
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
    }

}