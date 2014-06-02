package com.example.mailclient.app;

/**
 * Created by teo on 01/04/14.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.TableLayout;
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
*   TODO: comment code
*/

public class SendMailActivity extends Activity {

    private static final int SELECT_PICTURE = 1;
    ArrayList<String> selectedImagePath,attachmentList;
    EditText toEmailText, ccEmailText, bccEmailText, subjectEmailText, bodyEmailText;
    TextView attachmentView;
    LinearLayout.LayoutParams params;
    TableLayout lm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmailactivity);

        attachmentView = (TextView) this.findViewById(R.id.attachment);
        selectedImagePath = new ArrayList<String> ();
        attachmentList = new ArrayList<String> ();
        lm = (TableLayout) findViewById(R.id.array_button);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            selectedImagePath.add(getPath(selectedImageUri));

            //Show toast with "name of file" added
            File file = new File(getPath(selectedImageUri));
            String fileName=file.getName();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, fileName + " attached!", duration);
            toast.show();

            //concat filename to attachment's textview
            attachmentList.add(fileName);
            lm.removeAllViews();

            for (int i = 0; i < attachmentList.size(); i++) {
                // Create LinearLayout
                final LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);

                final Button btn = new Button(this);
                btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.view_attachment,0,0,0);
                btn.setText(new File(attachmentList.get(i)).getName());
                btn.setLayoutParams(params);
                btn.setId(i);
                final int indice = i;
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SendMailActivity.this);
                        // Add the buttons
                        alertDialog.setPositiveButton(R.string.view, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User views the attachment
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(selectedImagePath.get(indice))), "*/*");
                                startActivity(intent);
                            }
                        });
                        alertDialog.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User removes the attachment
                                selectedImagePath.remove(indice);
                                attachmentList.remove(indice);
                                ll.removeView(btn);
                            }
                        });
                        alertDialog.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User cancels the dialog
                            }
                        });
                        alertDialog.show();
                    }
                });

                //Add button to LinearLayout
                ll.addView(btn);
                //Add button to LinearLayout defined in XML
                lm.addView(ll);
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                this.finish();
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
        toEmailText = (EditText) this.findViewById(R.id.send_to_edit);
        ccEmailText = (EditText) this.findViewById(R.id.send_cc_edit);
        bccEmailText = (EditText) this.findViewById(R.id.send_bcc_edit);
        subjectEmailText = (EditText) this.findViewById(R.id.send_subject_edit);
        bodyEmailText = (EditText) this.findViewById(R.id.send_body);

        String fromEmail = Mailbox.account_email;
        String fromPassword = Mailbox.account_password;
        String toEmails = toEmailText.getText().toString();
        String ccEmails = ccEmailText.getText().toString();
        String bccEmails = bccEmailText.getText().toString();
        List<String> ccEmailList = null, bccEmailList = null;
        List<String> toEmailList = Arrays.asList(toEmails
                .split("\\s*,\\s*"));

        if (ccEmails != null || ccEmails != "") {
            ccEmailList = Arrays.asList(ccEmails.split("\\s*,\\s*"));
        }
        if (bccEmails != null || bccEmails != "") {
            bccEmailList = Arrays.asList(bccEmails.split("\\s*,\\s*"));
        }
        Log.i("SendMailActivity", "To List: " + toEmailList);
        String emailSubject = subjectEmailText.getText().toString();
        String emailBody = bodyEmailText.getText().toString();


        Log.i("Check", "stampiamo la size della lista: "+ selectedImagePath.size());
        if (selectedImagePath.size() == 0) {
            new SendMailTask(SendMailActivity.this).execute(fromEmail, toEmailList, emailSubject, emailBody, ccEmailList, bccEmailList);
        } else {
            new SendMailTask(SendMailActivity.this).execute(fromEmail, toEmailList, emailSubject, emailBody, ccEmailList, bccEmailList, selectedImagePath);
        }

        attachmentList.clear();

        ReceiveSentMailTask receive_sent_task = new ReceiveSentMailTask(this);
        receive_sent_task.execute(Mailbox.account_email, Mailbox.account_password);

    }

    public void addAttachment ( MenuItem menu ) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Document"), SELECT_PICTURE);
    }
}
