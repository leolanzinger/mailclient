package com.example.mailclient.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class SettingsActivity extends Activity {

    EditText usernameText, passwordText;
    TextView usernameView, passwordView;
    Button login_button;
    AuthPreferences authPreferences;
    private Uri uriContact;
    private String contactID;     // contacts unique ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        authPreferences = new AuthPreferences(this);

        usernameView = (TextView) this.findViewById(R.id.usernameView);
        passwordView = (TextView) this.findViewById(R.id.passwordView);

        usernameText = (EditText) this.findViewById(R.id.usernameText);
        passwordText = (EditText) this.findViewById(R.id.passwordText);

        usernameText.setText(authPreferences.getUser());
        passwordText.setText(authPreferences.getPassword());

        login_button = (Button) this.findViewById(R.id.confirm_button);
        login_button.setBackgroundResource(R.drawable.login_button);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkAccount(View v) {
        if (usernameText.getText().toString().matches("") || passwordText.getText().toString().matches("")) {
            // far partire un alert con errore di account o password sono nulli
            new AlertDialog.Builder(this)
                    .setTitle("Username or Password are empty")
                    .setMessage("Please insert correct credentials")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            CheckAccountTask checkaccount_task = new CheckAccountTask(SettingsActivity.this);
            checkaccount_task.execute(usernameText.getText(), passwordText.getText());
        }
    }

    public void accountSucceded(){
        Log.d("Check", "Account Succeded!");
        Log.d("Check", usernameText.getText().toString());
        Log.d("Check", passwordText.getText().toString());

        AuthPreferences authPreferences = new AuthPreferences(this);
        authPreferences.setUser(usernameText.getText().toString());
        authPreferences.setPassword(passwordText.getText().toString());
        Mailbox.account_email = authPreferences.getUser();
        Mailbox.account_password = authPreferences.getPassword();


        //Get owner info

        OwnerInfo ownerInfo = new OwnerInfo(this);

        Log.d("Check", ownerInfo.accountName);
        Log.d("Check", ownerInfo.email);
        Log.d("Check", ownerInfo.id);

        finish();

    }
    public void accountFailed(){
        new AlertDialog.Builder(this)
                .setTitle("Username or Password is wrong")
                .setMessage("Please insert correct credentials")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
