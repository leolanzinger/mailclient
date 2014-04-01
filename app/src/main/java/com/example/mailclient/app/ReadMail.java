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


public class ReadMail extends Activity {

    Email email;
    String from_addresses = "";
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

        email = MailClient.emailList.get(index);

        subject.setText(email.subject);
        date.setText(email.date.toString());

        Log.i("Reply",""+email.from[0]);

        for (int i=0; i<email.from.length; i++) {
            if (i == 0) {
                from_addresses = from_addresses.concat(String.valueOf(email.from[i]));
            }
            else {
                from_addresses = from_addresses.concat(", " + email.from[i]);
            }
        }
        from.setText(from_addresses);

        String body_content = "";

        for (int i=0; i<email.body.size(); i++) {
            body_content = body_content.concat(email.body.get(i));
        }
        body.setText(Html.fromHtml(body_content));



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.read_mail, menu);
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
    public void replyMail(View view) {
        Intent intent = new Intent(this, ReplyActivity.class);

        //parametri di from e password sono hardcodati, devo cercare di passare il destinatario della mail come fromEmail e la password vuota
        intent.putExtra("fromEmail","mailclientandroid@gmail.com");
        intent.putExtra("password","android2014");
        intent.putExtra("subject","Re: "+ email.subject);
        intent.putExtra("body",""+body.getText()); //DA INDENTARE
        intent.putExtra("to",from_addresses);
        startActivity(intent);
    }

}
