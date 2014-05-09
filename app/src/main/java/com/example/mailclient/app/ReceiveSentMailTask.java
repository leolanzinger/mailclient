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
import javax.mail.search.FlagTerm;

/*
*   Async Task to perform retrieving
*   operation of mails using GMailSender.java class
*/

public class ReceiveSentMailTask extends AsyncTask<Object, Object, ArrayList<Email>> {

    private Activity receiveMailActivity;

    public ReceiveSentMailTask(Activity activity) {
        receiveMailActivity = activity;

    }

    protected void onPreExecute() {
    }

    /*
    *   Execute task to receive sent email and print logs
    *   (Called before inbox list receive)
    */
    @Override
    protected ArrayList<Email> doInBackground(Object... args) {
        Message[] sent_msg = null;
        try {
            Log.i("ReceiveInboxTask", "About to instantiate GMailSender...");
            GMailReceiver reader = new GMailReceiver(args[0].toString(),
                    args[1].toString());
            try {
                sent_msg = reader.readSentMails();
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
        ArrayList<Email> list = new ArrayList<Email> ();

        if (sent_msg != null) {
            for (int i = 0; i < sent_msg.length; i++) {
                Email email = new Email();
                try {

                    email.setSubject(sent_msg[i].getSubject());
                    email.setDate(sent_msg[i].getSentDate());
                    email.setFrom(sent_msg[i].getFrom());
                    email.setTo(sent_msg[i].getRecipients(Message.RecipientType.TO));

                    //super hack yeee
                    if (sent_msg[i].getRecipients(Message.RecipientType.CC) != null) {
                        email.setCC(sent_msg[i].getRecipients(Message.RecipientType.CC));
                    } else {
                        email.setNoCC();
                    }

                    String ID = sent_msg[i].getHeader("Message-Id")[0];

                    email.setSeen();

                    email.setID(ID);

                    try {
                        email.setContent(sent_msg[i]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                list.add(email);
            }
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
         *  NB: insert into adapters other than list in reverse order
         */

        /*if (receiveMailActivity instanceof Sent ) {
            for (Email em : result) {
                if (!em.seen) {
                    Todo.adapter.insert(em, 0);
                }
                Mailbox.emailList.add(0, em);
            }
            Todo.adapter.notifyDataSetChanged();
            Mailbox.save(Mailbox.emailList);

            Todo.mPocketBar.progressiveStop();
            Todo.listView.onRefreshComplete();
            Todo.mPocketBar.setVisibility(View.GONE);
        }
        else */

        for (Email em : result) {
            Mailbox.sentList.add(0, em);
            Mailbox.saveSent(Mailbox.sentList);
        }


        super.onPostExecute(result);
    }

}