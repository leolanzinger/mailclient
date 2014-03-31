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


public class ReadMail extends Activity {

    Email email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mail);
        Bundle extras = getIntent().getExtras();
        int index = extras.getInt("index", 0);

        Button reply = (Button) findViewById(R.id.reply);
        TextView subject = (TextView) findViewById(R.id.read_subject);
        TextView date = (TextView) findViewById(R.id.read_date);
        TextView from = (TextView) findViewById(R.id.read_from);
        TextView body = (TextView) findViewById(R.id.read_body);

        email = MailClient.emailList.get(index);

        subject.setText(email.subject);
        date.setText(email.date.toString());

        String from_addresses = "";
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
        Intent intent = new Intent(this, SendMailActivity.class);
        startActivity(intent);
    }

}
