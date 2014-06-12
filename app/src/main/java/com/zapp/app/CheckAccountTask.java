package com.zapp.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

/*
*   Async task to sync deleted message flag
*/

public class CheckAccountTask extends AsyncTask {

    Activity parentActivity;
    boolean succeeded, password_checked;
    ProgressDialog progressDialog;

    public CheckAccountTask(Activity activity) {
        parentActivity = activity;
        succeeded = true;
        password_checked = true;
        progressDialog = ProgressDialog.show(parentActivity,"","Connecting");
    }


    protected void onPreExecute() {
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            GMailChecker checker = new GMailChecker(args[0].toString(),args[1].toString());
            succeeded = checker.connected();
            password_checked = checker.passwordChecked();
        } catch (Exception e) {
        }

        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
    }

    @Override
    public void onPostExecute(Object result) {


        if (parentActivity instanceof  MainActivity) {
            if(password_checked != true)
                ((MainActivity)parentActivity).passwordDialog();
        }

        else if (parentActivity instanceof LoginActivity) {
            if (succeeded && password_checked == true) {
                ((LoginActivity)parentActivity).accountSucceded();
            } else {
                ((LoginActivity)parentActivity).accountFailed();
            }
            progressDialog.dismiss();
        } else if (parentActivity instanceof SettingsActivity) {
            if (succeeded) {
                ((SettingsActivity)parentActivity).accountSucceded();
            } else {
                ((SettingsActivity)parentActivity).accountFailed();
            }
            progressDialog.dismiss();
        }
    }

}