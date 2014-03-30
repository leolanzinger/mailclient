package com.example.mailclient.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends Activity {

    ArrayList<Email> emailList;
    static EmailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailList  = new ArrayList<Email>();

        ListView listView = (ListView) findViewById(R.id.listView);

        adapter = new EmailAdapter(this, R.id.subject, emailList);
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public void sendEmail(View view) {
        Intent intent = new Intent(this, SendMailActivity.class);
        startActivity(intent);
    }

    public void receiveEmail(View view) {
        String account_email = "leonardo.lanzinger@gmail.com";
        String account_password = "leothebassist";
        ReceiveMailTask receive_task = new ReceiveMailTask(MainActivity.this);
        receive_task.execute(account_email, account_password);
    }
}
