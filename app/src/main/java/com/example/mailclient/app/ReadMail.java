package com.example.mailclient.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;

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
    TextView subject,date,from,body, to;
    Button attachmentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mail);
        Bundle extras = getIntent().getExtras();
        int index = extras.getInt("index", 0);

        subject = (TextView) findViewById(R.id.read_subject);
        date = (TextView) findViewById(R.id.read_date);
        from = (TextView) findViewById(R.id.read_from);
        to = (TextView) findViewById(R.id.read_to);
        body = (TextView) findViewById(R.id.read_body);
        attachmentView= (Button) findViewById(R.id.read_attachment);


        /*
         *  Set contents for the TextViews
         *  (subject, date, sender and content)
         */

        email = MailClient.emailList.get(index);
        subject.setText(email.subject);

        /*
         *  Parse date into dd MMMM yyyy format (e.g: 30 marzo 2014)
         */
        String date_format = new SimpleDateFormat("dd MMMM yyyy").format(email.date.getTime());
        date.setText(date_format);

        /*
         *  Fill up from address fields
         */
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
        from.setText(from_addresses);

        /*
         *  Fill up to address fields
         */
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
        to.setText(to_addresses);

        /*
         *  Parse body content from HTML String and
         *  display it as styled HTML text
         */
        String body_content = "";
        for (int i=0; i<email.body.size(); i++) {
            body_content = body_content.concat(email.body.get(i));
        }
        body.setText(Html.fromHtml(body_content));

        /*
         *  Notify IMAP server that the mail is read
         */
        if (!email.seen) {
            UpdateMailTask update_task = new UpdateMailTask(this);
            update_task.execute(email.ID);
            email.seen = true;
        }
//        View customNav = LayoutInflater.from(this).inflate(R.layout.topbar_readmail, null);
//        getActionBar().setCustomView(customNav);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.topbar_readmail, null);

        actionBar.setCustomView(v);

        attachmentView.setText(email.attachmentPath);

}


    //inflate the menu with custom actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_mail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     *  Launch new ReplyActivity
     *  that sends an email back to
     *  "from" addressess
     */
    public void replyMail(MenuItem menu) {
        Intent intent = new Intent(this, ReplyActivity.class);

        intent.putExtra("fromEmail",MailClient.account_email);
        intent.putExtra("password",MailClient.account_password);
        intent.putExtra("subject","Re: "+ email.subject);
        intent.putExtra("body",""+body.getText()); //DA INDENTARE
        intent.putExtra("to",from_addresses);
        startActivity(intent);
    }

    /*
     *  Unused showBodyMethod
     */
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

    public void showAttachment(View view){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Log.i("Attach", email.attachmentPath);
        intent.setDataAndType(Uri.fromFile(new File(email.attachmentPath)), "image/*");
        startActivity(intent);
    }

}
