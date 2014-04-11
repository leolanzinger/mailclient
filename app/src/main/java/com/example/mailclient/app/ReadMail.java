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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;

import javax.mail.internet.InternetAddress;

/*
 *  Activity that displays a single email
 *  - is triggered from main activity
 */

public class ReadMail extends Activity {

    Email email;
    String from_addresses = "";
    String to_addresses = "";
    String body_content;
    String body_content_html;
    TextView subject,date,from,to;
    WebView body;
    Button attachmentView;
    ImageButton todoButton;

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
        body = (WebView) findViewById(R.id.read_body);
        attachmentView= (Button) findViewById(R.id.read_attachment);


        /*
         *  Set contents for the TextViews
         *  (subject, date, sender and content)
         */

        email = Inbox.emailList.get(index);
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
        body_content = "";
        body_content_html = "<style type='text/css'>\n" +
                "       body {margin: 0 !important;} img {max-width: 100% !important;height:initial;} div,p,span,a {max-width: 100% !important;}\n" +
                "       </style>";
        for (int i=0; i<email.body.size(); i++) {
            body_content = body_content.concat(email.body.get(i));
            body_content_html = body_content_html.concat(email.body.get(i));
        }
        body.loadData(body_content_html, "text/html", "utf-8");
        body.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        body.getSettings().setJavaScriptEnabled(true);
        body.requestFocus(View.FOCUS_DOWN);

        /*
         *  Notify IMAP server that the mail is read
         */
        if (!email.seen) {
            UpdateMailTask update_task = new UpdateMailTask(this);
            update_task.execute(email.ID);
            email.seen = true;
        }


        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.topbar_readmail, null);


        /*
         *  To Do icon
         */
        todoButton = (ImageButton) v.findViewById(R.id.readMail_pin);
        if (email.todo) {
            todoButton.setBackgroundResource(R.drawable.pinned);
        }
        else {
            todoButton.setBackgroundResource(R.drawable.not_pinned);
        }

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
        intent.putExtra("fromEmail", Inbox.account_email);
        intent.putExtra("password", Inbox.account_password);
        intent.putExtra("subject","Re: "+ email.subject);
        intent.putExtra("body",""+Html.fromHtml(body_content).toString()); //DA INDENTARE
        intent.putExtra("to",from_addresses);
        startActivity(intent);
    }

    public void showAttachment(View view){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(email.attachmentPath)), "image/*");
        startActivity(intent);
    }

    /*
     *  Sets the to do variable or unset it if the email is already pinned
     */
    public void setToDo(View view) {
        if (email.todo) {
            email.removeTodo();
            todoButton.setBackgroundResource(R.drawable.not_pinned);
        }
        else {
            email.addTodo();
            todoButton.setBackgroundColor(R.color.yellow);
            todoButton.setBackgroundResource(R.drawable.pinned);
        }
        Inbox.save(Inbox.emailList);
    }
}
