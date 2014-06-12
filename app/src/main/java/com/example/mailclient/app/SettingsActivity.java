package com.example.mailclient.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class SettingsActivity extends Activity {

    EditText passwordText;
    TextView passwordView, usernameView;
    Spinner usernameSpinner;
    Button login_button;
    String username, name;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameView = (TextView) this.findViewById(R.id.usernameView);
        passwordView = (TextView) this.findViewById(R.id.passwordView);

        usernameSpinner = (Spinner) this.findViewById(R.id.usernameSpinner);
        passwordText = (EditText) this.findViewById(R.id.passwordText);

        login_button = (Button) this.findViewById(R.id.confirm_button);
        login_button.setBackgroundResource(R.drawable.login_button);
        login_button.setText("Change account");

        // ownerInfo per sapere tutti gli account disponibili sul telefono
        OwnerInfo ownerInfo = new OwnerInfo(this);
        String[] accounts_email = ownerInfo.retrieveEmailList();
        final String[] accounts_name = ownerInfo.retrieveNameList();

        ArrayAdapter<CharSequence> adapter;

        if (accounts_email == null || accounts_email.length == 0) {
            String[] empty_array = new String[1];
            empty_array[0] = "Aggiungi un account";
            adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, empty_array);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            usernameSpinner.setAdapter(adapter);
        }
        else {
            adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, accounts_email);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            usernameSpinner.setAdapter(adapter);

            // display current email in the spinner
            for (int i = 0; i < accounts_email.length; i++) {
                if (accounts_email[i].equals(Mailbox.account_email)) {
                    usernameSpinner.setSelection(i);
                }
            }

            // set spinner action listener and use it to change the layout of the account editor's textboxes
            usernameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    name = accounts_name[i];
                    if (Mailbox.account_email.equals(usernameSpinner.getItemAtPosition(i).toString())) {
                        login_button.setClickable(false);
                        login_button.setBackgroundResource(R.drawable.login_button_not_active);
                        passwordView.setTextColor(R.color.grey);
                    }
                    else {
                        login_button.setClickable(true);
                        login_button.setBackgroundResource(R.drawable.login_button);
                        passwordView.setTextColor(R.color.black);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void checkAccount(View v) {
        if (passwordText.getText().toString().matches("")) {
            // far partire un alert con errore di account o password sono nulli
            new AlertDialog.Builder(this)
                    .setTitle("Password is empty")
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
            username = usernameSpinner.getSelectedItem().toString();
            CheckAccountTask checkaccount_task = new CheckAccountTask(SettingsActivity.this);
            checkaccount_task.execute(username, passwordText.getText());
        }
    }

    public void accountSucceded(){
        Log.d("Check", "Account Succeded!");
        Log.d("Check", username);
        Log.d("Check", passwordText.getText().toString());

        new AlertDialog.Builder(this)
                .setTitle("Change account?")
                .setMessage("Previous account data will be lost")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AuthPreferences authPreferences = new AuthPreferences(MainActivity.baseContext);
                        authPreferences.setUser(username);
                        authPreferences.setPassword(passwordText.getText().toString());
                        authPreferences.setName(name);
                        Mailbox.account_email = authPreferences.getUser();
                        Mailbox.account_password = authPreferences.getPassword();
                        Mailbox.account_name = authPreferences.getName();
                        Mailbox.emailList.clear();
                        Intent finish_intent = new Intent();
                        setResult(Activity.RESULT_FIRST_USER, finish_intent);
                        finish();
                     }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }


}
