package com.example.mailclient.app;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by teo on 10/06/14.
 */
public class LoginActivity extends Activity{

    EditText usernameText, passwordText;
    TextView usernameView, passwordView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameView = (TextView) this.findViewById(R.id.usernameView);
        passwordView = (TextView) this.findViewById(R.id.passwordView);

        usernameText = (EditText) this.findViewById(R.id.usernameText);
        passwordText = (EditText) this.findViewById(R.id.passwordText);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void checkAccount(View v) {
        if (usernameText.getText().toString().matches("") || passwordText.getText().toString().matches("")) {
            // far partire un alert con errore di account o password sono nulli
            Log.d("Check", "Username or Password not valid!");
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
    }
    public void accountFailed(){
        Log.d("Check", "Account Failed!");
        Log.d("Check", usernameText.getText().toString());
        Log.d("Check", passwordText.getText().toString());
    }
}
