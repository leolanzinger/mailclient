package com.example.mailclient.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

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
    public static Mailbox mailbox;
    static EmailAdapter adapter;
    public static Context baseContext;
    public static SmoothProgressBar mPocketBar;
    public static PullToRefreshListView listView;
    public static Button refresh_button;
    public static ArrayList<Email> todo_list;
    private Animator animator;

    /*
     *  Drawer menu variables
     */
    private String[] mDrawerLinks;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    int list_position, list_visible_position;
    View child_focused;

    public Todo() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_list);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        baseContext = getBaseContext();
        mailbox = new Mailbox(baseContext);

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
                if (i == 0) {
                    mDrawerLayout.closeDrawers();
                }
                else if (i == 1) {
                    Intent intent;
                    intent = new Intent(Todo.this, Inbox.class);
                    startActivity(intent);
                }
                else if (i == 2) {
                    Intent intent;
                    intent = new Intent(Todo.this, Sent.class);
                    startActivity(intent);
                }
                else if (i == 3) {
                    Intent intent;
                    intent = new Intent(Todo.this, TrashBin.class);
                    startActivity(intent);
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
        for (int i=0; i<Mailbox.emailList.size(); i++) {
            if ((Mailbox.emailList.get(i).todo || !Mailbox.emailList.get(i).seen) && (!Mailbox.emailList.get(i).deleted)) {
                todo_list.add(Mailbox.emailList.get(i));
            }
            else {}
        }
        adapter = new EmailAdapter(this, R.id.list_subject, todo_list){
            /*
             * Used to get visible position of the list item in the adapter
             * (different from actual position in the list) !
             */
            @Override
            public void processPosition(View view) {
                list_position = listView.getPositionForView(view);
                list_visible_position = list_position - listView.getFirstVisiblePosition();
                child_focused = view;
            }
        };
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.empty_todo));

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


        animator = new Animator();

        /*
         * Add touch listener to swipe and unpin
         * email from the listview
         */
        listView.setOnTouchListener(new SwipeDetector(0) {
            @Override
            public void getResults() {
                if (this.swipeDetected()){
                    if (this.getAction().equals(SwipeDetector.Action.LR_TRIGGER)) {
                        // do the onSwipe action
                        animator.swipeTodo(child_focused, list_position - 1);
                        Email email = Mailbox.emailList.get(Mailbox.emailList.indexOf(todo_list.get(list_position - 1)));
                        email.removeTodo();
                        if (!email.seen) {
                            UpdateSeenMailTask update_task = new UpdateSeenMailTask(Todo.this);
                            update_task.execute(email.ID);
                            email.seen = true;
                        }
                    }
                    else if (this.getAction().equals(SwipeDetector.Action.CLICK)) {
                        // open email
                        Intent intent = new Intent(Todo.this, ReadMail.class);
                        int index = Mailbox.emailList.indexOf(todo_list.get(list_position - 1));
                        intent.putExtra("index", index);
                        startActivity(intent);
                    }
                    else if (this.getAction().equals(SwipeDetector.Action.LR_BACK)) {
                        // reset view
                        animator.resetView(listView.getChildAt(list_visible_position));
                    }
                    else {

                    }
                }
            }
        });

        mDrawerLayout.closeDrawers();
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
        todo_list.clear();
        for (int i=0; i<Mailbox.emailList.size(); i++) {
            if (Mailbox.emailList.get(i).todo || !Mailbox.emailList.get(i).seen) {
                todo_list.add(Mailbox.emailList.get(i));
            }
            else {}
        }
        adapter.notifyDataSetChanged();
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onPause() {
        super.onPause();
        Mailbox.save(Mailbox.emailList);
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
        ReceiveSentMailTask receive_sent_task = new ReceiveSentMailTask(Todo.this);
        receive_sent_task.execute(Mailbox.account_email, Mailbox.account_password);
        ReceiveInboxTask receive_task = new ReceiveInboxTask(Todo.this);
        receive_task.execute(Mailbox.account_email, Mailbox.account_password);
    }

    public void receiveMail(View view) {
        refresh_button.setVisibility(View.GONE);
        mPocketBar.setVisibility(View.VISIBLE);
        mPocketBar.progressiveStart();
        ReceiveSentMailTask receive_sent_task = new ReceiveSentMailTask(Todo.this);
        receive_sent_task.execute(Mailbox.account_email, Mailbox.account_password);
        ReceiveInboxTask receive_task = new ReceiveInboxTask(Todo.this);
        receive_task.execute(Mailbox.account_email, Mailbox.account_password);
    }

}

