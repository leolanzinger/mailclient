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
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
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
    ImageButton todoButton;
    int mail_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mail);
        Bundle extras = getIntent().getExtras();
        mail_index = extras.getInt("index", 0);

        subject = (TextView) findViewById(R.id.read_subject);
        date = (TextView) findViewById(R.id.read_date);
        from = (TextView) findViewById(R.id.read_from);
        to = (TextView) findViewById(R.id.read_to);
        body = (WebView) findViewById(R.id.read_body);


        /*
         *  Set contents for the TextViews
         *  (subject, date, sender and content)
         */

        email = Mailbox.emailList.get(mail_index);
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
        body_content_html = "";
        for (int i=0; i<email.body.size(); i++) {
            body_content = body_content.concat(email.body.get(i));
            body_content_html = body_content_html.concat(email.body.get(i));
        }
//        body.loadData(body_content_html, "text/html", "UTF-8");
        body.loadDataWithBaseURL(null, body_content_html, "text/html", "utf-8", null);

//        body.setWebViewClient(new myWebViewClient());

        body.setWebChromeClient(new WebChromeClient());
        body.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        body.getSettings().setJavaScriptEnabled(true);
        body.requestFocus(View.FOCUS_DOWN);

        /*
         *  Notify IMAP server that the mail is read
         */
        if (!email.seen) {
            UpdateSeenMailTask update_task = new UpdateSeenMailTask(this);
            update_task.execute(email.ID);
            email.seen = true;
        }


        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

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


        //TODO: handle multiple attachments

        if (email.attachmentPath.size()!=0){
            final TableLayout lm = (TableLayout) findViewById(R.id.array_button);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < email.attachmentPath.size(); i++) {

                // Create LinearLayout
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);

                Button btn = new Button(this);
                btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.view_attachment,0,0,0);
                btn.setText(new File(email.attachmentPath.get(i)).getName());
                btn.setLayoutParams(params);
                btn.setId(i);
                final int indice = i;
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(email.attachmentPath.get(indice))), "image/*");
                        startActivity(intent);

                    }
                });
                //Add button to LinearLayout
                ll.addView(btn);
                //Add button to LinearLayout defined in XML
                lm.addView(ll);

            }
        }
}
    private class myWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("mailto:")) {
//                MailTo mt = MailTo.parse(url);
//                TODO: handle mailto aprendo una SendMailActivity compilata
//                Intent i = newEmailIntent(MyActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
//                startActivity(i);
                view.reload();
                return true;
            } else if (url.startsWith("http:") ||  (url.startsWith("https:"))) {
//              Open browser with simple links
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
            else {
                Log.d("Check", "check url: " +url);
                view.loadUrl(url);
            }
            return true;
        }
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
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                this.finish();
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
        intent.putExtra("fromEmail", Mailbox.account_email);
        intent.putExtra("password", Mailbox.account_password);
        intent.putExtra("subject","Re: "+ email.subject);
        intent.putExtra("body",""+Html.fromHtml(body_content).toString()); //DA INDENTARE
        intent.putExtra("to",from_addresses);
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
        Mailbox.save(Mailbox.emailList);
    }

    public void deleteMail(View view) {
        if (email.todo) {
            Todo.todo_list.remove(Todo.todo_list.indexOf(email));
        }
        Mailbox.emailList.get(mail_index).deleted = true;
        UpdateDeletedMailTask update_deleted_task = new UpdateDeletedMailTask(ReadMail.this);
        update_deleted_task.execute(email.ID);
        this.finish();
    }
}
