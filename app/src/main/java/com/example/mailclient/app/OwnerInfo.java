package com.example.mailclient.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by teo on 11/06/14.
 */

public class OwnerInfo {
    // this class allows to get device information. It's done in two steps:
    // 1) get synchronization account email
    // 2) get contact data, associated with this email
    // by https://github.com/jehy


    //WARNING! You need to have permissions
    //
    //<uses-permission android:name="android.permission.READ_CONTACTS" />
    //<uses-permission android:name="android.permission.GET_ACCOUNTS" />
    //
    // in your AndroidManifest.xml for this code.

    public String phone = null;
    public String accountName = null;
    public String name = null;

    final AccountManager manager;
    final Account[] accounts;
    ArrayList<String> emails, id, names;

    public OwnerInfo(Activity MainActivity) {
        manager = AccountManager.get(MainActivity);
        accounts = manager.getAccountsByType("com.google");

        emails = new ArrayList<String>();
        id = new ArrayList<String>();
        names = new ArrayList<String>();

        // retrieve accounts info
        if (accounts[0].name != null) {

            accountName = accounts[0].name;
            String where= ContactsContract.CommonDataKinds.Email.DATA + " = ?";
            ArrayList<String> what = new ArrayList<String>();
            what.add(accountName);
            Log.v("Got account", "Account " + accountName);
            for (int i=1;i<accounts.length;i++)
            {
                where+=" or "+ContactsContract.CommonDataKinds.Email.DATA + " = ?";
                what.add(accounts[i].name);
                Log.v("Got account", "Account " + accounts[i].name);
            }

            String[] whatarr = (String[]) what.toArray(new String[what.size()]);
            ContentResolver cr = MainActivity.getContentResolver();
            Cursor emailCur = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    where,
                    whatarr, null);

            while (emailCur.moveToNext()) {
                if (!emails.contains(emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)))) {
//                id[j] = (emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)));
                    emails.add(emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
                    // add to arraylist relative primary name
                    if (!names.contains(emailCur.getString(emailCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)))) {
                        names.add(emailCur.getString(emailCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                    }
                    // add to arraylist relative id
                    if (!id.contains(emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)))) {
                        id.add(emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)));
                    }
                }
            }

            emailCur.close();

            emails.add("Add new account");
        }
        else {
            emails.add("Add new account");
        }
    }

    public String[] retrieveEmailList() {
        return emails.toArray(new String[emails.size()]);
    }

    public String[] retrieveNameList() { return names.toArray(new String[names.size()]); }

    public String[] retrieveIdList() { return id.toArray(new String[id.size()]); }
}