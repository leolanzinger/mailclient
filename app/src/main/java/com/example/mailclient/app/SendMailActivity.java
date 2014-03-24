package com.example.mailclient.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.mail.Message;
import javax.mail.MessagingException;

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
        final TextView received_mail = (TextView) this.findViewById(R.id.received_mail);

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
                ReceiveMailTask receive_task = new ReceiveMailTask(SendMailActivity.this);
                receive_task.execute(account_email, account_password);
//                try {
//                    Message msg_ret = receive_task.get();
//                    Log.i("mail", msg_ret.getSubject().toString());
////                    received_mail.setText(msg_ret.getSubject().toString());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (MessagingException e) {
//                    e.printStackTrace();
//                }
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
