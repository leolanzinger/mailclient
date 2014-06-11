package com.example.mailclient.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

/*
*   Async task to sync deleted message flag
*/

public class CheckAccountTask extends AsyncTask {

    Activity parentActivity;
    boolean succeeded;
    ProgressDialog progressDialog;

    public CheckAccountTask(Activity activity) {
        parentActivity = activity;
        succeeded = true;
        progressDialog = ProgressDialog.show(parentActivity,"","Connecting");
    }


    protected void onPreExecute() {
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            GMailChecker checker = new GMailChecker(args[0].toString(),args[1].toString());
            succeeded = checker.connected();
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
            if (succeeded) {
                ((LoginActivity)parentActivity).accountSucceded();
            } else {
                ((LoginActivity)parentActivity).accountFailed();
            }
            progressDialog.dismiss();
        }
    }

}