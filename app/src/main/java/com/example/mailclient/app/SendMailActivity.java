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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
*   Send and receive emails activity.
*   Async tasks are used because Android doesn't
*   allow Network operation in the main thread.
*/

public class SendMailActivity extends Activity {

//    private static final int READ_REQUEST_CODE = 42;

    private static final int SELECT_PICTURE = 1;
    ArrayList<String> selectedImagePath,attachmentList;
    EditText toEmailText, ccEmailText, subjectEmailText, bodyEmailText;
    TextView attachmentView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmailactivity);

        attachmentView = (TextView) this.findViewById(R.id.attachment);
        selectedImagePath = new ArrayList<String> ();
        attachmentList = new ArrayList<String> ();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Log.d("URI VAL", "selectedImageUri = " + selectedImageUri.toString());
            selectedImagePath.add(getPath(selectedImageUri));

          //Show toast with "name of file" added
            File file = new File(getPath(selectedImageUri));
            String fileName=file.getName();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, fileName + " attached!", duration);
            toast.show();

            //concat filename to attachment's textview
            attachmentList.add(fileName);
            attachmentView.setText(attachmentList.toString());

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

    public void sendEmail(MenuItem menu) {


//        final TextView received_mail = (TextView) this.findViewById(R.id.received_mail);

        toEmailText = (EditText) this.findViewById(R.id.send_to_edit);
        ccEmailText = (EditText) this.findViewById(R.id.send_cc_edit);
        subjectEmailText = (EditText) this.findViewById(R.id.send_subject_edit);
        bodyEmailText = (EditText) this.findViewById(R.id.send_body);

        String fromEmail = Inbox.account_email;
        String fromPassword = Inbox.account_password;
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

        attachmentList.clear();
    }

    public void addAttachment ( MenuItem menu ) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Document"), SELECT_PICTURE);
    }
}
