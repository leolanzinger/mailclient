package com.example.mailclient.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends Activity {

    ArrayList<Email> emailList;
    static EmailAdapter adapter;
    static InternalStorage storer;
    static String KEY = "mailClient";

    public static Context baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseContext = getBaseContext();

        storer = new InternalStorage();
        emailList  = new ArrayList<Email>();

        ListView listView = (ListView) findViewById(R.id.listView);

        adapter = new EmailAdapter(this, R.id.subject, emailList);
        listView.setAdapter(adapter);

        try {
            ArrayList<Email> cachedEntries = (ArrayList<Email>) InternalStorage.readObject(this, KEY);
            adapter.addAll(cachedEntries);
            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public static void save(ArrayList<Email> result) {
        try {
            storer.writeObject(baseContext, KEY, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
