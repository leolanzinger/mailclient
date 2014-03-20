package com.example.mailclient.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;

import javax.mail.Message;

/*
*   Send and receive emails activity.
*   Async tasks are used because Android doesn't
*   allow Network operation in the main thread.
*/

public class SendMailActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmailactivity);

        /*
        *   Buttons for receiving / sending emails
        */
        final Button send = (Button) this.findViewById(R.id.button1);
        final Button receive = (Button) this.findViewById(R.id.button2);

        /*
        *   Add listener to "send email" button and call
        *   async task to perform send mail tasks
        */
        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i("SendMailActivity", "Send Button Clicked.");

                String fromEmail = "leonardo.lanzinger@gmail.com";
                String fromPassword = "leothebassist";
                String toEmails = "michelon.giulio@gmail.com";
                List<String> toEmailList = Arrays.asList(toEmails
                        .split("\\s*,\\s*"));
                Log.i("SendMailActivity", "To List: " + toEmailList);
                String emailSubject = "lesbicone";
                String emailBody = "kasabian";
                new SendMailTask(SendMailActivity.this).execute(fromEmail,
                        fromPassword, toEmailList, emailSubject, emailBody);
            }
        });

        /*
        *   Add listener to "receive email" button and call
        *   async task to perform receive mail tasks
        */
        receive.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                String account_email = "leonardo.lanzinger@gmail.com";
                String account_password = "leothebassist";
                new ReceiveMailTask(SendMailActivity.this).execute(account_email, account_password);
            }
        });
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

}
