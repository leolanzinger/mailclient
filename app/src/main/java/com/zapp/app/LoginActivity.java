package com.zapp.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mailclient.app.R;

/**
 * Created by teo on 10/06/14.
 */
public class LoginActivity extends Activity{

    EditText passwordText;
    TextView passwordView, usernameView;
    Spinner usernameSpinner;
    Button login_button;
    String username;
    String[] account_names, account_ids;

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

        // ownerInfo per sapere tutti gli account disponibili sul telefono
        OwnerInfo ownerInfo = new OwnerInfo(this);
        final String[] accounts_email = ownerInfo.retrieveEmailList();
        account_names = ownerInfo.retrieveNameList();
        account_ids = ownerInfo.retrieveIdList();

        ArrayAdapter<CharSequence> adapter;

        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, accounts_email);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usernameSpinner.setAdapter(adapter);

        usernameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == (accounts_email.length - 1)) {
                    startAddGoogleAccountIntent(getBaseContext());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

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
            CheckAccountTask checkaccount_task = new CheckAccountTask(LoginActivity.this);
            checkaccount_task.execute(username, passwordText.getText());
        }
    }

    public void accountSucceded(){
        Log.d("Check", "Account Succeded!");
        Log.d("Check", username);
        Log.d("Check", passwordText.getText().toString());

        // save shared prefernces
        AuthPreferences authPreferences = new AuthPreferences(this);
        authPreferences.setUser(username);
        authPreferences.setPassword(passwordText.getText().toString());
        authPreferences.setName(account_names[usernameSpinner.getSelectedItemPosition()]);
        authPreferences.setId(account_ids[usernameSpinner.getSelectedItemPosition()]);

        // save default starting time
        authPreferences.setStart(8, 0);
        authPreferences.setEnd(18, 0);

        // put everything to Mailbox
        Mailbox.scheduler_start = authPreferences.getStart();
        Mailbox.scheduler_end = authPreferences.getEnd();
        Mailbox.account_email = authPreferences.getUser();
        Mailbox.account_password = authPreferences.getPassword();
        Mailbox.account_name = authPreferences.getName();
        Mailbox.account_id = authPreferences.getId();

        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
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

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }

    public static void startAddGoogleAccountIntent(Context context)
    {
        Intent addAccountIntent = new Intent(android.provider.Settings.ACTION_ADD_ACCOUNT)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        addAccountIntent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[] {"com.google"});
        context.startActivity(addAccountIntent);
    }
}
