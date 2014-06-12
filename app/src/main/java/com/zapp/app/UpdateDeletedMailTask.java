package com.zapp.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

/*
*   Async task to sync deleted message flag
*/

public class UpdateDeletedMailTask extends AsyncTask {

    Activity sendMailActivity;
    private boolean deleted_check;
    boolean password_checked;



    public UpdateDeletedMailTask(Activity activity, boolean deleted) {
        sendMailActivity = activity;
        deleted_check = deleted;
        password_checked = true;
    }

    protected void onPreExecute() {
    }

    @Override
    protected Object doInBackground(Object... args) {


        try {
            /*
             *  Updates INBOX status of updated deleted Emails
             */
            GMailUpdater updater = new GMailUpdater(Mailbox.account_email, Mailbox.account_password);
            password_checked = updater.passwordChecked();
            if (password_checked != false) {
                try {
                    ArrayList<String> deleted = new ArrayList<String>();
                    deleted.add((String) args[0]);
                    updater.updateDeletedGmail(deleted, deleted_check);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            if (sendMailActivity instanceof MainActivity) ((MainActivity)sendMailActivity).passwordDialog();
            else if (sendMailActivity instanceof ReadMail) ((ReadMail)sendMailActivity).passwordDialog();
            Log.d("Check", "UpdateDeletedMailTask");
        } else {
            if (sendMailActivity instanceof ReadMail) sendMailActivity.finish();
        }

    }

}