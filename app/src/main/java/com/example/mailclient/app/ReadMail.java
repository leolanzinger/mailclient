package com.example.mailclient.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

/*
 *  Activity that displays a single email
 *  - is triggered from main activity
 */

public class ReadMail extends Activity {

    Email email;
    String from_addresses = "";
    String to_addresses = "";
    TextView subject,date,from,body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mail);
        Bundle extras = getIntent().getExtras();
        int index = extras.getInt("index", 0);

        Button reply = (Button) findViewById(R.id.reply);
        subject = (TextView) findViewById(R.id.read_subject);
        date = (TextView) findViewById(R.id.read_date);
        from = (TextView) findViewById(R.id.read_from);
        body = (TextView) findViewById(R.id.read_body);

        /*
         *  Set contents for the TextViews
         *  (subject, date, sender and content)
         */

        email = MailClient.emailList.get(index);

        subject.setText(email.subject);
        from.setText(from_addresses);

        /*
         *  Parse date into dd - MMM format (e.g: 30 mar)
         */
        date.setText(email.date.toString());

        for (int i=0; i<email.from.length; i++) {
            if (i == 0) {
                String s = email.from == null ? null : ((InternetAddress) email.from[i]).getAddress();
                from_addresses = from_addresses.concat(s);

            }
            else {
                from_addresses = from_addresses.concat(", ");
                String s = email.from == null ? null : ((InternetAddress) email.from[i]).getAddress();
                from_addresses = from_addresses.concat(s);
            }
        }

        for (int i=0; i<email.to.length; i++) {
            if (i == 0) {
                String s = email.to == null ? null : ((InternetAddress) email.to[i]).getAddress();
                to_addresses = to_addresses.concat(s);

            }
            else {
                to_addresses = to_addresses.concat(", ");
                String s = email.from == null ? null : ((InternetAddress) email.to[i]).getAddress();
                to_addresses = to_addresses.concat(s);
            }
        }
        try {
            showBody(email.body);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
<<<<<<< HEAD
        body.setText(Html.fromHtml(body_content));

        /*
         *  Notify IMAP server that the mail is read
         */
        if (!email.seen) {
            UpdateMailTask update_task = new UpdateMailTask(this);
            update_task.execute(email.ID);
        }
=======

//        /*
//         *  Parse body content from HTML String and
//         *  display it as styled HTML text
//         */
//        String body_content = "";
//        for (int i=0; i<email.body.size(); i++) {
//            body_content = body_content.concat(email.body.get(i));
//        }
//        body.setText(Html.fromHtml(body_content));
>>>>>>> 8f3ad69cc23e56dee40c5f675248572377669592
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_mail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void replyMail(View view) {
        Intent intent = new Intent(this, ReplyActivity.class);

        intent.putExtra("fromEmail",MailClient.account_email);
        intent.putExtra("password",MailClient.account_password);
        intent.putExtra("subject","Re: "+ email.subject);
        intent.putExtra("body",""+body.getText()); //DA INDENTARE
        intent.putExtra("to",from_addresses);
        startActivity(intent);
    }
    public void showBody(MimeMultipart fullBody) throws MessagingException {

        MimeBodyPart messageBodyPart;
        String body_content = "";

        for (int i=0; i<fullBody.getCount(); i++) {
            messageBodyPart = (MimeBodyPart) fullBody.getBodyPart(i); //prende ad ogni ciclo for un part del messaggio

            String disposition = messageBodyPart.getDisposition();

            //attachment here!
            if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) {
                System.out.println("Mail have some attachment");

                DataHandler handler = messageBodyPart.getDataHandler();
                System.out.println("file name : " + handler.getName());
                //buttiamo fuori il multipart, mi sa che lo dovremo salvare e mostrare dalla "cache"
            }
            else {
                //è testo, quindi lo concateno!
                body_content = body_content.concat(messageBodyPart.toString());  // the changed code
            }
        }
        body.setText(Html.fromHtml(body_content));

    }

}
