package com.example.mailclient.app;

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

public class SendMailActivity extends Activity {

    private static final int SELECT_PICTURE = 1;
    ArrayList<String> selectedImagePath;
    public static ArrayList<Message> messageBox;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmailactivity);

        /*
        *   Buttons for receiving / sending emails
        */
        final Button send = (Button) this.findViewById(R.id.button1);
        final Button receive = (Button) this.findViewById(R.id.button2);
        final Button attach = (Button) this.findViewById(R.id.button3);
        final TextView received_mail = (TextView) this.findViewById(R.id.received_mail);

        final EditText toEmailText = (EditText) this.findViewById(R.id.editText3);
        final EditText subjectEmailText = (EditText) this.findViewById(R.id.editText4);
        final EditText bodyEmailText = (EditText) this.findViewById(R.id.editText5);
        selectedImagePath = new ArrayList<String>();


        /*
        *   Add listener to "send email" button and call
        *   async task to perform send mail tasks
        */
        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i("SendMailActivity", "Send Button Clicked.");

                //Scegliere se usare dati già hardcodati o da editare negli edittext

//                String fromEmail = "leonardo.lanzinger@gmail.com";
//                String fromPassword = "leothebassist";
//                String toEmails = "matteolever@me.com";
//                List<String> toEmailList = Arrays.asList(toEmails
//                        .split("\\s*,\\s*"));
//                Log.i("SendMailActivity", "To List: " + toEmailList);
//                String emailSubject = "soggettodiprova";
//                String emailBody = "parapatutiti";


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
                    new SendMailTask(SendMailActivity.this).execute(fromEmail, fromPassword, toEmailList, emailSubject, emailBody);
                }
                else {
                    new SendMailTask(SendMailActivity.this).execute(fromEmail, fromPassword, toEmailList, emailSubject, emailBody, selectedImagePath);
                }
                selectedImagePath=new ArrayList<String>();
            }
        });

        /*
        *   Add listener to "receive email" button and call
        *   async task to perform receive mail tasks
        */
        receive.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                String account_email = "mailclientandroid@gmail.com";
                String account_password = "android2014t";
                ReceiveMailTask receive_task = new ReceiveMailTask(SendMailActivity.this);
                receive_task.execute(account_email, account_password);
            }
        });

        attach.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // in onCreate or any event where your want the user to
                // select a file
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Document"), SELECT_PICTURE);

            }
        });
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
        return super.onOptionsItemSelected(item);
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
}
