package com.example.mailclient.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class MainActivity extends Activity {

    private static final int LOGIN_SUCCEDED = 1;
    private static final int SETTINGS_RESULT = 2;
    /*
    *   Set main variables
    */
    public static Mailbox mailbox;
    public static Context baseContext;
    public static int current_fragment;
    public static String TAG;
    static boolean first_open = true;

    /*
     *  Drawer menu variables
     */
    String[] mDrawerLinks;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    TextView surnameTextView, nameTextView;
    ImageView profileImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_list);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        baseContext = getBaseContext();
        mailbox = new Mailbox(baseContext);

        TAG = "TODO";

        /*
         *  Implement drawer layout and adapters
         */
        mDrawerLinks = getResources().getStringArray(R.array.sidebar_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the header
        View header = (View)getLayoutInflater().inflate(R.layout.drawer_header_container,null);
        mDrawerList.addHeaderView(header);

        mDrawerList.setAdapter(new DrawerAdapter(this, R.layout.drawer_list, mDrawerLinks));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //put user info on the drawer
        nameTextView = (TextView)findViewById(R.id.nome);
        surnameTextView = (TextView)findViewById(R.id.cognome);
        profileImageView = (ImageView) findViewById(R.id.imageView);

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
         *  Istantiate fragment
         */
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        TodoFragment fragment = new TodoFragment();
        fragmentTransaction.add(R.id.main_content, fragment, TAG);
        fragmentTransaction.commit();

        /*
         * Check login data
         */

        AuthPreferences authPreferences = new AuthPreferences(this);

        if (authPreferences.getUser() != null && authPreferences.getPassword() != null) {
            Mailbox.account_email = authPreferences.getUser();
            Mailbox.account_password = authPreferences.getPassword();
            Mailbox.account_name = authPreferences.getName();
            Mailbox.scheduler_start = authPreferences.getStart();
            Mailbox.scheduler_end = authPreferences.getEnd();
            Mailbox.account_id = authPreferences.getId();
            setCredentialsToDrawer();
            first_open = false;
        } else {
            //Facciamo partire un intent che te lo fa aggiungere
            first_open = true;
            Intent intentAccount = new Intent(this, LoginActivity.class);
            startActivityForResult(intentAccount, LOGIN_SUCCEDED);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                this.finish();
            }
            else if (resultCode == Activity.RESULT_OK) {
                Log.d("Check", "Receive mail? Yes!");
                nameTextView.setText("");
                surnameTextView.setText("");
                profileImageView.setImageResource(R.drawable.avatar);
                receiveMail();
                setCredentialsToDrawer();
            }
        }
        else if (requestCode == 2) {
            if (resultCode == Activity.RESULT_FIRST_USER) {
                Log.d("result code", "reset user");
                receiveMail();
                nameTextView.setText("");
                surnameTextView.setText("");
                profileImageView.setImageResource(R.drawable.avatar);
                setCredentialsToDrawer();
                mDrawerLayout.closeDrawers();
            }
        }

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onPause() {
        super.onPause();
        Mailbox.save(Mailbox.emailList);
        Log.i("lifecycle", "onPause main activity");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i("lifecycle", "onDestroy main activity");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i("lifecycle", "onResume main activity");
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.i("lifecycle", "onStart main activity");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.i("lifecycle", "onStop main activity");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            Intent intent = new Intent(baseContext, SettingsActivity.class);
            startActivityForResult(intent, SETTINGS_RESULT);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
     *  The first is triggered from main activity,
     *  the second is triggered from refresh_button
     */
    public void receiveMail(SmoothProgressBar mPocketBar) {
        mPocketBar.setVisibility(View.VISIBLE);
        mPocketBar.progressiveStart();
        ReceiveSentMailTask receive_sent_task = new ReceiveSentMailTask(MainActivity.this);
        ReceiveInboxTask receive_task = new ReceiveInboxTask(MainActivity.this);
        receive_sent_task.execute(Mailbox.account_email, Mailbox.account_password);
        receive_task.execute(Mailbox.account_email, Mailbox.account_password);
    }

    public void receiveMail(View view) {
        SmoothProgressBar mPocketBar = (SmoothProgressBar) getFragmentManager().findFragmentById(R.id.main_content).getView().findViewById(R.id.pocket);
        mPocketBar.setVisibility(View.VISIBLE);
        mPocketBar.progressiveStart();
        ReceiveSentMailTask receive_sent_task = new ReceiveSentMailTask(MainActivity.this);
        ReceiveInboxTask receive_task = new ReceiveInboxTask(MainActivity.this);
        receive_sent_task.execute(Mailbox.account_email, Mailbox.account_password);
        receive_task.execute(Mailbox.account_email, Mailbox.account_password);
    }

    public void receiveMail() {
        SmoothProgressBar mPocketBar = (SmoothProgressBar) getFragmentManager().findFragmentById(R.id.main_content).getView().findViewById(R.id.pocket);
        mPocketBar.setVisibility(View.VISIBLE);
        mPocketBar.progressiveStart();
        ReceiveSentMailTask receive_sent_task = new ReceiveSentMailTask(MainActivity.this);
        ReceiveInboxTask receive_task = new ReceiveInboxTask(MainActivity.this);
        receive_sent_task.execute(Mailbox.account_email, Mailbox.account_password);
        receive_task.execute(Mailbox.account_email, Mailbox.account_password);
    }

    /*
     * Drawer listener for click events
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /*
     * This method is called when the user clicks
     * on an item of the drawer pane
     */
    private void selectItem(int position) {
        if (position == current_fragment) {
            mDrawerLayout.closeDrawers();
        }
        else {
            Fragment fragment = null;
            FragmentManager fragmentManager = getFragmentManager();
            switch (position) {
                case 1:
                    fragment = new TodoFragment();
                    TAG = "TODO";
                    break;
                case 2:
                    fragment = new InboxFragment();
                    TAG = "INBOX";
                    break;
                case 3:
                    fragment = new SentFragment();
                    TAG = "SENT";
                    break;
                case 4:
                    fragment = new TrashBinFragment();
                    TAG = "TRASH";
                    break;
            }
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            ft.replace(R.id.main_content, fragment, TAG);
            ft.commit();

        }
    }

    /*
     * Call this method from fragment to set
     * panel title
     */
    public void setActionBarTitle(String title){
        setTitle(title);
    }

    //open settings from drawer
    public void openSettings (View v) {
        Intent intent = new Intent(baseContext, SettingsActivity.class);
        startActivityForResult(intent, SETTINGS_RESULT);
    }

    public void setCredentialsToDrawer() {
        String arr[] = Mailbox.account_name.split(" ", 2);
        //becca il nome
        String firstWord = arr[0];
        if (arr.length == 2) {
            String theRest = arr[1];
            surnameTextView.setText(theRest);
        } else {
            if (firstWord.contains("@")) {
                firstWord = firstWord.split("@", 2)[0];
            }
        }
        nameTextView.setText(firstWord);
        openPhoto(Long.valueOf(Mailbox.account_id));

    }

    public void openPhoto(long contactId) {
        Bitmap bitmap = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            profileImageView.setImageResource(R.drawable.avatar);
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(data), null, bmOptions);
                    profileImageView.setImageDrawable(new RoundedAvatarDrawable(bitmap));
                }
            }
        } finally {
            cursor.close();
        }
    }
}
