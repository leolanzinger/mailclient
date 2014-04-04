package com.example.mailclient.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.search.FlagTerm;

import static javax.mail.Flags.Flag.SEEN;

/*
*   Async Task to perform retrieving
*   operation of mails using GMailSender.java class
*/

public class ReceiveMailTask extends AsyncTask<Object, Object, ArrayList<Email>> {

    private Activity sendMailActivity;

    public ReceiveMailTask(Activity activity) {
        sendMailActivity = activity;

    }

    protected void onPreExecute() {
    }

    /*
    *   Execute task to receive email and print logs
    */
    @Override
    protected ArrayList<Email> doInBackground(Object... args) {
        Message[] msg = null;
        try {
            Log.i("ReceiveMailTask", "About to instantiate GMailSender...");
            GMailReader reader = new GMailReader(args[0].toString(),
                    args[1].toString());
            try {
                msg = reader.readLastMails();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            publishProgress(e.getMessage());
        }

        /*
         *  We don't want a plain Message[] array without content
         *  so let's parse each Message into an ArrayList<Email>
         *  and fill the content (currently only subject and date).
         *
         *  NB: without calling explicitly getSubject() and getDate()
         *  the Message object would be empty.
         */
        ArrayList<String> unread_mess_ID = new ArrayList<String> ();
        ArrayList<Email> list = new ArrayList<Email> ();
        for (int i=0; i < msg.length; i++) {
            Email email = new Email();
            try {
                email.setSubject(msg[i].getSubject());
                email.setDate(msg[i].getSentDate());
                email.setFrom(msg[i].getFrom());
                email.setTo(msg[i].getAllRecipients());

                String ID = msg[i].getHeader("Message-Id")[0];

                Flags seen = new Flags(Flags.Flag.SEEN);
                FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
                if (unseenFlagTerm.match(msg[i])) {
                    email.setUnSeen();
                    unread_mess_ID.add(ID);
                }
                else {
                    email.setSeen();
                }


                email.setID(ID);

                try {
                    Object multipart = msg[i].getContent();
                    if (!(multipart instanceof Multipart)) {
                        Log.i("multipart", "è una stringa");
                        email.setContentToString(multipart.toString());
                    }
                    else {
                        Log.i("multipart", "è un multipart");
                        email.setContent((Multipart) multipart);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            list.add(email);
        }

        GMailUpdater updater = new GMailUpdater(MailClient.account_email, MailClient.account_password);
        try {
            updater.updateGmail(unread_mess_ID, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void onProgressUpdate(Object... values) {
    }

    @Override
    public void onPostExecute(ArrayList<Email> result) {

        /*
         *  Notify adapter of the retrieved mails,
         *  store them into cache and hide progress
         *  bar
         */
        for (Email em : result) {
            MailClient.adapter.insert(em, 0);
        }
        MailClient.adapter.notifyDataSetChanged();
        MailClient.save(MailClient.emailList);
        MailClient.mPocketBar.progressiveStop();
        MailClient.listView.onRefreshComplete();
        MailClient.mPocketBar.setVisibility(View.GONE);
        super.onPostExecute(result);
    }

}