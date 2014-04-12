package com.example.mailclient.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import eu.erikw.PullToRefreshListView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

/*
 *  Activity that displays the to do panel
 */
public class Todo extends Activity {

    /*
    *   Set main variables
    */
    static EmailAdapter adapter;
    public static Context baseContext;
    public static SmoothProgressBar mPocketBar;
    public static PullToRefreshListView listView;
    public static Button refresh_button;
    private static ArrayList<Email> todo_list;

    /*
     *  Drawer menu variables
     */
    private String[] mDrawerLinks;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    public Todo() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_list);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        baseContext = getBaseContext();

        /*
         *  Implement drawer layout and adapters
         */
        mDrawerLinks = getResources().getStringArray(R.array.sidebar_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new DrawerAdapter(this, R.layout.drawer_list, mDrawerLinks));
        // TODO: complete the drawer listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    finish();
                }
                else if (i == 0) {
                    mDrawerLayout.closeDrawers();
                }
                else {
                }
            }
        });

         /*
         *  Drawer adapter and list
         */
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, R.drawable.ic_drawer , R.string.drawer_open, R.string.drawer_close){
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

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
         *  Instantiate to do array list and its adapter
         */
        todo_list = new ArrayList<Email>();
        for (int i=0; i<Inbox.emailList.size(); i++) {
            if (Inbox.emailList.get(i).todo || !Inbox.emailList.get(i).seen) {
                todo_list.add(Inbox.emailList.get(i));
            }
            else {}
        }
        adapter = new EmailAdapter(this, R.id.list_subject, todo_list);
        listView.setAdapter(adapter);

        /*
         *  Set refresh button if email list is empty
         */
        refresh_button = (Button) findViewById(R.id.refresh_button);
        if (adapter.isEmpty() || adapter == null) {
            refresh_button.setVisibility(View.VISIBLE);
        }

        /*
         *  Open email when clicking on email in the list
         */
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(Todo.this, ReadMail.class);
//                // TODO: trovare indice dentro a Inbox.emailList
//                int pos = Inbox.emailList.indexOf(todo_list.get(i));
//                intent.putExtra("index", pos);
//                startActivity(intent);
//            }
//        });


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
                    if (todo_list.get(position).todo ) {
                        if (swipeDetector.getAction().equals(SwipeDetector.Action.LR)) {
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(baseContext, "unpinned", duration);
                            toast.show();
                            Inbox.emailList.get(Inbox.emailList.indexOf(todo_list.get(position))).removeTodo();
                            todo_list.remove(position);
                            adapter.notifyDataSetChanged();
                            checkEmpty();
                        }
                    }
                    else {
                        if (swipeDetector.getAction().equals(SwipeDetector.Action.LR)) {
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(baseContext, "pinned", duration);
                            toast.show();
                            Inbox.emailList.get(Inbox.emailList.indexOf(todo_list.get(position))).addTodo();
                            adapter.notifyDataSetChanged();
                        }
                        else if (swipeDetector.getAction().equals(SwipeDetector.Action.RL)) {
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(baseContext, "archieved", duration);
                            toast.show();
                            Inbox.emailList.get(Inbox.emailList.indexOf(todo_list.get(position))).setSeen();
                            todo_list.remove(position);
                            UpdateMailTask update_task = new UpdateMailTask(Todo.this);
                            update_task.execute(Inbox.emailList.get(position).ID);
                            adapter.notifyDataSetChanged();
                            checkEmpty();
                        }
                    }
                } else {
                    Intent intent = new Intent(Todo.this, ReadMail.class);
                // TODO: trovare indice dentro a Inbox.emailList
                    int pos = Inbox.emailList.indexOf(todo_list.get(position));
                    intent.putExtra("index", pos);
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
        for(int i=0; i<todo_list.size(); i++) {
            if (!todo_list.get(i).todo && todo_list.get(i).seen) {
                todo_list.remove(todo_list.get(i));
            }
        }
        adapter.notifyDataSetChanged();
        checkEmpty();
    }

    @Override
    public void onPause() {
        super.onPause();
        Inbox.save(Inbox.emailList);
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
        ReceiveMailTask receive_task = new ReceiveMailTask(Todo.this);
        receive_task.execute(Inbox.account_email, Inbox.account_password);
    }

    public void receiveMail(View view) {
        refresh_button.setVisibility(View.GONE);
        mPocketBar.setVisibility(View.VISIBLE);
        mPocketBar.progressiveStart();
        ReceiveMailTask receive_task = new ReceiveMailTask(Todo.this);
        receive_task.execute(Inbox.account_email, Inbox.account_password);
    }

    public static void updateList(int new_emails) {
        for(int i=0; i<new_emails; i++) {
            if (!Inbox.emailList.get(i).seen) {
                adapter.insert(Inbox.emailList.get(i), 0);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public static void checkEmpty() {
        if (todo_list.isEmpty()) {
            refresh_button.setVisibility(View.VISIBLE);
        }
    }
}

