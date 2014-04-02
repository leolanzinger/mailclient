package com.example.mailclient.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.search.FlagTerm;

import static javax.mail.Flags.Flag.SEEN;

/*
*   Async task to sync messages flags - not used yet
*/

public class UpdateMailTask extends AsyncTask {

    private Activity sendMailActivity;

    public UpdateMailTask(Activity activity) {
        sendMailActivity = activity;

    }

    protected void onPreExecute() {
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            Log.i("UpdateMailTask", "About to instantiate GMailUpdater...");
            GMailUpdater updater = new GMailUpdater(MailClient.account_email, MailClient.account_password);
            try {
                ArrayList<String> seen = new ArrayList<String>();
                seen.add((String) args[0]);
                updater.updateGmail(seen, true);
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