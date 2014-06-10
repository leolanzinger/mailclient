package com.example.mailclient.app;

import android.app.Activity;
import android.os.AsyncTask;

/*
*   Async task to sync deleted message flag
*/

public class CheckAccountTask extends AsyncTask {

    Activity parentActivity;
    boolean succeded;

    public CheckAccountTask(Activity activity) {
        parentActivity = activity;
        succeded = true;
    }


    protected void onPreExecute() {
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            GMailChecker checker = new GMailChecker(args[0].toString(),args[1].toString());
            succeded = checker.connected();
        } catch (Exception e) {
        }

        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
    }

    @Override
    public void onPostExecute(Object result) {
        if (parentActivity instanceof LoginActivity) {
            if (succeded) {
                ((LoginActivity)parentActivity).accountSucceded();
            } else {
                ((LoginActivity)parentActivity).accountFailed();
            }
        }
    }

}