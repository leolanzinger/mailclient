package com.example.mailclient.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.FlagTerm;

/*
*   Async Task to perform retrieving Inbox folder
*   operation of mails using GMailSender.java class
*/

public class ReceiveInboxTask extends AsyncTask<Object, Object, ArrayList<Email>> {

    private Activity receiveMailActivity;

    public ReceiveInboxTask(Activity activity) {
        receiveMailActivity = activity;

    }

    protected void onPreExecute() {
    }

    /*
    *   Execute task to receive email and print logs
    */
    @Override
    protected ArrayList<Email> doInBackground(Object... args) {
        Message[] msg = null;
        Message[] sent_msg = null;
        try {

            GMailReceiver reader = new GMailReceiver(args[0].toString());
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

        if (msg != null) {
            for (int i = 0; i < msg.length; i++) {
                Email email = new Email();
                try {
                    // check if email is deleted
                    Flags deleted = new Flags(Flags.Flag.DELETED);
                    FlagTerm deletedFlagTerm = new FlagTerm(deleted, true);
                    if (deletedFlagTerm.match(msg[i])) {
                        email.deleted = true;
                    }
                    email.setSubject(msg[i].getSubject());
                    email.setDate(msg[i].getSentDate());
                    email.setFrom(msg[i].getFrom());
                    email.setTo(msg[i].getRecipients(Message.RecipientType.TO));

                    // avoid null pointer on cc
                    if (msg[i].getRecipients(Message.RecipientType.CC) != null) {
                        email.setCC(msg[i].getRecipients(Message.RecipientType.CC));
                    } else {
                        email.setNoCC();
                    }

                    String ID = msg[i].getHeader("Message-Id")[0];

                    Flags seen = new Flags(Flags.Flag.SEEN);
                    FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
                    if (unseenFlagTerm.match(msg[i])) {
                        email.setUnSeen();
                        unread_mess_ID.add(ID);
                    } else {
                        email.setSeen();
                    }

                    email.todo = false;

                    email.setID(ID);

                    try {
                        email.setContent(msg[i]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                list.add(email);
            }

            GMailUpdater updater = new GMailUpdater(Mailbox.account_email);
            try {
                updater.updateSeenGmail(unread_mess_ID, false);
            } catch (Exception e) {
                e.printStackTrace();
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
         *  bar. Check which adapter to fill.
         *  NB: insert into adapters other than list in reverse order
         */

        Fragment todo_fragment = receiveMailActivity.getFragmentManager().findFragmentByTag("TODO");
        Fragment inbox_fragment = receiveMailActivity.getFragmentManager().findFragmentByTag("INBOX");
        Fragment sent_fragment = receiveMailActivity.getFragmentManager().findFragmentByTag("SENT");
        Fragment trash_fragment = receiveMailActivity.getFragmentManager().findFragmentByTag("TRASH");

        if (todo_fragment != null && todo_fragment.isVisible()) {
            for (Email em : result) {
                if (!em.seen) {
                    TodoFragment.adapter.insert(em, 0);
                }
                Mailbox.emailList.add(0, em);
            }
            TodoFragment.adapter.notifyDataSetChanged();
            Mailbox.save(Mailbox.emailList);

            TodoFragment.mPocketBar.progressiveStop();
            TodoFragment.listView.onRefreshComplete();
            TodoFragment.mPocketBar.setVisibility(View.GONE);
        }
        else if (inbox_fragment != null && inbox_fragment.isVisible()) {
            for (Email em : result) {
                if (!em.deleted) {
                    InboxFragment.adapter.insert(em, 0);
                }
                Mailbox.emailList.add(0, em);
            }
            InboxFragment.adapter.notifyDataSetChanged();
            Mailbox.save(Mailbox.emailList);

            InboxFragment.mPocketBar.progressiveStop();
            InboxFragment.listView.onRefreshComplete();
            InboxFragment.mPocketBar.setVisibility(View.GONE);
        }
        else if (sent_fragment != null && sent_fragment.isVisible()) {
            for (Email em : result) {
                Mailbox.emailList.add(0, em);
            }
            Mailbox.save(Mailbox.emailList);

            SentFragment.mPocketBar.progressiveStop();
            SentFragment.listView.onRefreshComplete();
            SentFragment.mPocketBar.setVisibility(View.GONE);
        }
        else if (trash_fragment != null && trash_fragment.isVisible()) {
            for (Email em : result) {
                if (em.deleted) {
                    TrashBinFragment.adapter.insert(em, 0);
                }
                Mailbox.emailList.add(0, em);
            }
            TrashBinFragment.adapter.notifyDataSetChanged();
            Mailbox.save(Mailbox.emailList);

            TrashBinFragment.mPocketBar.progressiveStop();
            TrashBinFragment.listView.onRefreshComplete();
            TrashBinFragment.mPocketBar.setVisibility(View.GONE);
        }
        super.onPostExecute(result);
    }

}