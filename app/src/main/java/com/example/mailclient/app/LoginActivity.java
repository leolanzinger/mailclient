package com.example.mailclient.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by teo on 10/06/14.
 */
public class LoginActivity extends Activity{

    EditText passwordText;
    TextView passwordView, usernameView;
    Spinner usernameSpinner;
    Button login_button;
    String username;

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
        String[] accounts_email = ownerInfo.retrieveEmailList();

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
        }
//        Log.d("spinner", usernameSpinner.getSelectedItem().toString());

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

        AuthPreferences authPreferences = new AuthPreferences(this);
        authPreferences.setUser(username);
        authPreferences.setPassword(passwordText.getText().toString());
        Mailbox.account_email = authPreferences.getUser();
        Mailbox.account_password = authPreferences.getPassword();


//
//        TextView nome = (TextView)findViewById(R.id.nome);
//        nome.setText(ownerInfo.name);

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
