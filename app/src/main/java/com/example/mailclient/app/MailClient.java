package com.example.mailclient.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import eu.erikw.PullToRefreshListView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

import com.example.mailclient.app.DrawerAdapter;



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

    /*
     *  Drawer menu variables
     */
    private String[] mDrawerLinks;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    public MailClient() {
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         *  Implement drawer layout and adapters
         */
        mDrawerLinks = getResources().getStringArray(R.array.sidebar_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new DrawerAdapter(this, R.layout.drawer_list, mDrawerLinks));
        // Set the list's click listener
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, R.drawable.ic_drawer , R.string.drawer_open, R.string.drawer_close){
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

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
         *  Open email when clicking on email in the list, now it's on the swipeDetector stuff
         */
//         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//             @Override
//             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                 Intent intent = new Intent(MailClient.this, ReadMail.class);
//                 int index = i;
//                 intent.putExtra("index", index);
//                 startActivity(intent);
//             }
//         });


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

        /*
        this stuff is from http://stackoverflow.com/questions/4373485/android-swipe-on-list/9340202#9340202
        and it should help us somehow to swipe stuff here and there.
        */
        //    final ListView listView = getListView();
        final SwipeDetector swipeDetector = new SwipeDetector();
        listView.setOnTouchListener(swipeDetector);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (swipeDetector.swipeDetected()){
                    // do the onSwipe action
                    Log.i("suaipa","suaipa");

                } else {
                    // do the onItemClick action
                    Intent intent = new Intent(MailClient.this, ReadMail.class);
                    int index = position;
                    intent.putExtra("index", index);
                    startActivity(intent);

                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
                if (swipeDetector.swipeDetected()){
                    // do the onSwipe action
                    Log.i("suaipa","suaipa");
                    return true;

                } else {
                    // do the onItemLongClick action
                    Log.i("onItemLongClick","onItemLongClick");
                    return true;
                }
            }
        });
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("on resume", "resumed");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        save(emailList);
    }

    /*
     *  Triggered from send email button:
     *  call send email activity
     */
    public void sendEmail(MenuItem menu) {
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
