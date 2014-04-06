package com.example.mailclient.app;

/**
 * Created by teo on 01/04/14.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.Message;



/*
*   Send and receive emails activity.
*   Async tasks are used because Android doesn't
*   allow Network operation in the main thread.
*/

public class ReplyActivity extends Activity {

    private static final int SELECT_PICTURE = 1;
    ArrayList<String> selectedImagePath;
    EditText toEmailText, ccEmailText, subjectEmailText, bodyEmailText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmailactivity);


        toEmailText = (EditText) this.findViewById(R.id.send_to_edit);
        ccEmailText = (EditText) this.findViewById(R.id.send_cc_edit);
        subjectEmailText = (EditText) this.findViewById(R.id.send_subject_edit);
        bodyEmailText = (EditText) this.findViewById(R.id.send_body);
        selectedImagePath = new ArrayList<String>();

        //E LAVORARE QUA PER PRENDERLI COME REPLY
        final Intent intent=getIntent();
        subjectEmailText.setText(intent.getStringExtra("subject"));
        toEmailText.setText(intent.getStringExtra("to"));

        /*
         *  Set quoted original message
         */
        String body_cont = intent.getStringExtra("body");
        bodyEmailText.setText("\nOriginal message: \n\n" + body_cont);
        bodyEmailText.requestFocus();
        bodyEmailText.setSelection(0);


        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                Log.d("URI VAL", "selectedImageUri = " + selectedImageUri.toString());
                selectedImagePath.add(getPath(selectedImageUri));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_mail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        else {
            this.finish();
            return true;
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {  MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null) {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        else
            return uri.getPath();               // FOR OI/ASTRO/Dropbox etc
    }

    public void sendEmail(MenuItem menu) {


//        final TextView received_mail = (TextView) this.findViewById(R.id.received_mail);

        toEmailText = (EditText) this.findViewById(R.id.send_to_edit);
        ccEmailText = (EditText) this.findViewById(R.id.send_cc_edit);
        subjectEmailText = (EditText) this.findViewById(R.id.send_subject_edit);
        bodyEmailText = (EditText) this.findViewById(R.id.send_body);

        String fromEmail = MailClient.account_email;
        String fromPassword = MailClient.account_password;
        String toEmails = toEmailText.getText().toString();
        List<String> toEmailList = Arrays.asList(toEmails
                .split("\\s*,\\s*"));
        Log.i("SendMailActivity", "To List: " + toEmailList);
        String emailSubject = subjectEmailText.getText().toString();
        String emailBody = bodyEmailText.getText().toString();


        Log.i("Check", "stampiamo la size della lista: "+ selectedImagePath.size());
        if (selectedImagePath.size()==0){
            new SendMailTask(ReplyActivity.this).execute(fromEmail, fromPassword, toEmailList, emailSubject, emailBody);
        }
        else {
            new SendMailTask(ReplyActivity.this).execute(fromEmail, fromPassword, toEmailList, emailSubject, emailBody, selectedImagePath);
        }
    }

    public void addAttachment ( MenuItem menu ) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Document"), SELECT_PICTURE);
    }
}
