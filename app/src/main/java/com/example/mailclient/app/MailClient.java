package com.example.mailclient.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;

import eu.erikw.PullToRefreshListView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;



public class MailClient extends Activity {

    /*
    *   Set main variables
    */
    static ArrayList<Email> emailList;
    static EmailAdapter adapter;
    static InternalStorage storer;
    static String KEY = "mailClient";
    public static String account_email = "mailclientandroid@gmail.com";
    public static String account_password = "android2014";

    public static Context baseContext;
    public static SmoothProgressBar mPocketBar;
    public static PullToRefreshListView listView;
    public static Button refresh_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         *  Needed for saving methods
         */
        baseContext = getBaseContext();
        storer = new InternalStorage();
        emailList  = new ArrayList<Email>();

        /*
         *  Instantiate pullable listView
         */
        listView = (PullToRefreshListView) findViewById(R.id.listView);
        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                receiveEmail();
            }
        });

        /*
         *  Instantiate list adapter
         */
        adapter = new EmailAdapter(this, R.id.list_subject, emailList);
        listView.setAdapter(adapter);

        /*
         *  Load previously cached email objects from internal storage
         */
        try {
            ArrayList<Email> emailList = (ArrayList<Email>) InternalStorage.readObject(this, KEY);
            adapter.addAll(emailList);
            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (adapter.isEmpty() || adapter == null) {
            refresh_button = (Button) findViewById(R.id.refresh_button);
            refresh_button.setVisibility(View.VISIBLE);
        }

        /*
         *  Open email when clicking on email in the list
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MailClient.this, ReadMail.class);
                int index = i;
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });


        /*
         *  Istantiate progress bar and hide it
         */
        mPocketBar = (SmoothProgressBar) findViewById(R.id.pocket);
        mPocketBar.setSmoothProgressDrawableBackgroundDrawable(
                SmoothProgressBarUtils.generateDrawableWithColors(
                        getResources().getIntArray(R.array.pocket_background_colors),
                        ((SmoothProgressDrawable) mPocketBar.getIndeterminateDrawable()).getStrokeWidth()));
        mPocketBar.setVisibility(View.GONE);
        mPocketBar.progressiveStop();
    }


    //inflate the menu with custom actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
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
     *  Triggered from send email button:
     *  call send email activity
     */
    public void sendEmail(View view) {
        Intent intent = new Intent(this, SendMailActivity.class);
        startActivity(intent);
    }

    /*
     *  Execute receive mail async task
     *  and triggers progress bar.
     *  The firs is triggered from main activity,
     *  the second is triggered from refresh_button
     */
    public void receiveEmail() {
        mPocketBar.setVisibility(View.VISIBLE);
        mPocketBar.progressiveStart();
        ReceiveMailTask receive_task = new ReceiveMailTask(MailClient.this);
        receive_task.execute(account_email, account_password);
    }

    public void receiveMail(View view) {
        refresh_button.setVisibility(View.GONE);
        mPocketBar.setVisibility(View.VISIBLE);
        mPocketBar.progressiveStart();
        ReceiveMailTask receive_task = new ReceiveMailTask(MailClient.this);
        receive_task.execute(account_email, account_password);
    }

    /*
     *  Triggered from ReceiveMailTask onPostExecute method:
     *  store retrieved mail into internal storage
     */
    public static void save(ArrayList<Email> result) {
        try {
            storer.writeObject(baseContext, KEY, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
