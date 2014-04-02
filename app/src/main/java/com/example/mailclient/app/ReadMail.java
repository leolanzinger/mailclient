package com.example.mailclient.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
                from_addresses = from_addresses.concat(String.valueOf(email.from[i]));
            }
            else {
                from_addresses = from_addresses.concat(", " + email.from[i]);
            }
        }

        for (int i=0; i<email.to.length; i++) {
            if (i == 0) {
                to_addresses = to_addresses.concat(String.valueOf(email.to[i]));
            }
            else {
                to_addresses = to_addresses.concat(", " + email.to[i]);
            }
        }



        /*
         *  Parse body content from HTML String and
         *  display it as styled HTML text
         */
        String body_content = "";
        for (int i=0; i<email.body.size(); i++) {
            body_content = body_content.concat(email.body.get(i));
        }
        body.setText(Html.fromHtml(body_content));
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

        //parametri di from e password sono hardcodati, devo cercare di passare il destinatario della mail come fromEmail e la password vuota
        Log.i("Reply","check email.to: "+to_addresses);
        intent.putExtra("fromEmail",to_addresses);
        intent.putExtra("password","");
        intent.putExtra("subject","Re: "+ email.subject);
        intent.putExtra("body",""+body.getText()); //DA INDENTARE
        intent.putExtra("to",from_addresses);
        startActivity(intent);
    }

}
