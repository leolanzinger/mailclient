package com.example.mailclient.app;

/*
 * Created by teo on 01/04/14.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
*/

public class ReplyActivity extends Activity {

    private static final int OLDERVERSION = 0;
    private static final int NEWVERSION = 1;
    ArrayList<String> selectedImagePath,attachmentList;
    EditText toEmailText, ccEmailText, subjectEmailText, bodyEmailText;
    TextView attachmentView;
    LinearLayout.LayoutParams params;
    TableLayout lm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmailactivity);


        toEmailText = (EditText) this.findViewById(R.id.send_to_edit);
        ccEmailText = (EditText) this.findViewById(R.id.send_cc_edit);
        subjectEmailText = (EditText) this.findViewById(R.id.send_subject_edit);
        bodyEmailText = (EditText) this.findViewById(R.id.send_body);
        selectedImagePath = new ArrayList<String>();

        final Intent intent = getIntent();
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

        attachmentView = (TextView) this.findViewById(R.id.attachment);
        attachmentList = new ArrayList<String> ();

        lm = (TableLayout) findViewById(R.id.array_button);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


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
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReplyActivity.this);
                        // Add the buttons
                        alertDialog.setPositiveButton(R.string.view, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User views the attachment
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(selectedImagePath.get(indice))), "image/*");
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
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else
            return uri.getPath();               // FOR OI/ASTRO/Dropbox etc
    }

    public void sendEmail(MenuItem menu) {

        toEmailText = (EditText) this.findViewById(R.id.send_to_edit);
        ccEmailText = (EditText) this.findViewById(R.id.send_cc_edit);
        subjectEmailText = (EditText) this.findViewById(R.id.send_subject_edit);
        bodyEmailText = (EditText) this.findViewById(R.id.send_body);

        String fromEmail = Mailbox.account_email;
        String fromPassword = Mailbox.account_password;
        String toEmails = toEmailText.getText().toString();
        List<String> toEmailList = Arrays.asList(toEmails
                .split("\\s*,\\s*"));
        Log.i("SendMailActivity", "To List: " + toEmailList);
        String emailSubject = subjectEmailText.getText().toString();
        String emailBody = bodyEmailText.getText().toString();


        Log.i("Check", "stampiamo la size della lista: " + selectedImagePath.size());
        if (selectedImagePath.size() == 0) {
            new SendMailTask(ReplyActivity.this).execute(fromEmail, fromPassword, toEmailList, emailSubject, emailBody);
        } else {
            new SendMailTask(ReplyActivity.this).execute(fromEmail, fromPassword, toEmailList, emailSubject, emailBody, selectedImagePath);
        }
    }

    public void addAttachment(MenuItem menu) {
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, OLDERVERSION);
        } else {
            // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
            // browser.
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

            // Filter to only show results that can be "opened", such as a
            // file (as opposed to a list of contacts or timezones)
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // Filter to show only images, using the image MIME data type.
            // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
            // To search for all documents available via installed storage providers,
            // it would be "*/*".
            intent.setType("image/*");
        }
    }
}
