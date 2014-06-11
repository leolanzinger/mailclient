package com.example.mailclient.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by teo on 10/06/14.
 */
public class LoginActivity extends Activity{

    EditText usernameText, passwordText;
    TextView usernameView, passwordView;
    Button login_button;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameView = (TextView) this.findViewById(R.id.usernameView);
        passwordView = (TextView) this.findViewById(R.id.passwordView);

        usernameText = (EditText) this.findViewById(R.id.usernameText);
        passwordText = (EditText) this.findViewById(R.id.passwordText);

        login_button = (Button) this.findViewById(R.id.confirm_button);
        login_button.setBackgroundResource(R.drawable.login_button);
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
            CheckAccountTask checkaccount_task = new CheckAccountTask(LoginActivity.this);
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

        OwnerInfo ownerInfo = new OwnerInfo(this);

        Log.d("Check", ownerInfo.name); //Dobbiamo buttarlo nella barra laterale
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
