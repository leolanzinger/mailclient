package com.example.mailclient.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by teo on 10/06/14.
 */

public class AuthPreferences {

    private static final String KEY_USER = "user";
    private static final String KEY_PASSWORD = "password";

    private SharedPreferences preferences;

    public AuthPreferences(Context context) {
        preferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
    }

    public void setUser(String user) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USER, user);
        editor.commit();
    }

    public void setPassword(String password) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    public String getUser() {
        return preferences.getString(KEY_USER, null);
    }

    public String getPassword() {
        return preferences.getString(KEY_PASSWORD, null);
    }
}