package com.example.mailclient.app;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.mail.internet.InternetAddress;

/*
 *  Activity that displays a single email
 *  - is triggered from main activity
 *  TODO: comment code
 */

public class ReadMail extends FragmentActivity {

    Email email;
    String from_addresses = "";
    String to_addresses = "";
    String cc_addresses = "";
    String body_content;
    String body_content_html;
    TextView subject,date,from,to,cc;
    WebView body;
    ImageButton todoButton;
    int mail_index;
    boolean call_from_sent, call_from_inbox, call_from_todo;

    PopupWindow popup;

    // request code for speech recognition
    private static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_mail);
        Bundle extras = getIntent().getExtras();
        mail_index = extras.getInt("index", 0);

        /*
         * We need this to avoid pinning and deleting
         * of sent emails
         */
        call_from_sent = false;
        call_from_sent = extras.getBoolean("sent");

        /*
         *  We need those to know from which fragment
         *  we are reading our mail
         */
        call_from_inbox = false;
        call_from_todo = false;
        call_from_inbox = extras.getBoolean("inbox");
        call_from_todo = extras.getBoolean("todo");

        subject = (TextView) findViewById(R.id.read_subject);
        date = (TextView) findViewById(R.id.read_date);
        from = (TextView) findViewById(R.id.read_from);
        to = (TextView) findViewById(R.id.read_to);
        body = (WebView) findViewById(R.id.read_body);
        cc = (TextView) findViewById(R.id.read_cc);

        /*
         *  If the caller activity is Sent Folder
         *  get email from Mailbox.sentList, otherwise
         *  from Mailbox.emailList
         */
        if ( call_from_sent ) {
            email = Mailbox.sentList.get(mail_index);
        }
        else {
            email = Mailbox.emailList.get(mail_index);
        }

        /*
         *  Set contents for the TextViews
         *  (subject, date, sender and content)
         */
        subject.setText(email.subject);

        /*
         *  Parse date into dd MMMM yyyy format (e.g: 30 marzo 2014)
         */
        String date_format = new SimpleDateFormat("dd MMMM yyyy").format(email.date.getTime());
        date.setText(date_format);

        /*
         *  Fill up from address fields
         */
        for (int i = 0; i < email.from.length; i++) {
            if (i == 0) {
                String s = email.from == null ? null : ((InternetAddress) email.from[i]).getAddress();
                from_addresses = from_addresses.concat(s);

            } else {
                from_addresses = from_addresses.concat(", ");
                String s = email.from == null ? null : ((InternetAddress) email.from[i]).getAddress();
                from_addresses = from_addresses.concat(s);
            }
        }
        from.setText(from_addresses);

        /*
         *  Fill up to address fields
         */
        for (int i = 0; i < email.to.length; i++) {
            if (i == 0) {
                String s = email.to == null ? null : ((InternetAddress) email.to[i]).getAddress();
                to_addresses = to_addresses.concat(s);

            } else {
                to_addresses = to_addresses.concat(", ");
                String s = email.from == null ? null : ((InternetAddress) email.to[i]).getAddress();
                to_addresses = to_addresses.concat(s);
            }
        }
        to.setText(to_addresses);

         /*
         *  Fill up cc address fields
         */
        for (int i = 0; i < email.cc.length; i++) {
            if (i == 0) {
                String s = email.cc == null ? null : ((InternetAddress) email.cc[i]).getAddress();
                cc_addresses = cc_addresses.concat(s);

            } else {
                cc_addresses = cc_addresses.concat(", ");
                String s = email.cc == null ? null : ((InternetAddress) email.cc[i]).getAddress();
                cc_addresses = cc_addresses.concat(s);
            }
        }
        cc.setText(cc_addresses);

        /*
         *  Parse body content from HTML String and
         *  display it as styled HTML text
         */
        body_content = "";
        body_content_html = "";
        for (int i = 0; i < email.body.size(); i++) {
            body_content = body_content.concat(email.body.get(i));
            body_content_html = body_content_html.concat(email.body.get(i));
        }
        body.loadDataWithBaseURL("", body_content_html, "text/html", "utf-8", "");

        body.setWebViewClient(new myWebViewClient());
        body.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        body.getSettings().setJavaScriptEnabled(true);
        body.requestFocus(View.FOCUS_DOWN);

        /*
         *  Notify IMAP server that the mail is read
         */
        if (!email.seen) {
            UpdateSeenMailTask update_task = new UpdateSeenMailTask(this);
            update_task.execute(email.ID);
            email.seen = true;
        }


        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.topbar_readmail, null);


        /*
         *  To Do icon
         */
        todoButton = (ImageButton) v.findViewById(R.id.readMail_pin);
        if (email.todo) {
            todoButton.setBackgroundResource(R.drawable.pinned);
        } else {
            todoButton.setBackgroundResource(R.drawable.not_pinned);
        }

        actionBar.setCustomView(v);

        if (email.attachmentPath.size() != 0) {

            final TableLayout lm = (TableLayout) findViewById(R.id.array_button);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            for (int i = 0; i < email.attachmentPath.size(); i++) {

                // Create LinearLayout
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);

                Button btn = new Button(this);
                btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.view_attachment, 0, 0, 0);
                btn.setText(new File(email.attachmentPath.get(i)).getName());
                btn.setLayoutParams(params);
                btn.setId(i);
                final int indice = i;
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(email.attachmentPath.get(indice))), "image/*");
                        startActivity(intent);

                    }
                });
                //Add button to LinearLayout
                ll.addView(btn);
                //Add button to LinearLayout defined in XML
                lm.addView(ll);

            }
        }
}


    private class myWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("mailto:")) {
    //                MailTo mt = MailTo.parse(url);
    //                TODO: handle mailto aprendo una SendMailActivity compilata
    //                Intent i = newEmailIntent(MyActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
    //                startActivity(i);
                view.reload();
                return true;
            } else if (url.startsWith("http:") ||  (url.startsWith("https:"))) {
    //              Open browser with simple links
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
            else {
                Log.d("Check", "check url: " +url);
                view.loadUrl(url);
            }
            return true;
        }
    }

    //inflate the menu with custom actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_mail, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     *  Show options menu for
     *  reply mail button
     */
    public void showOptionsMenu(MenuItem menu) {
        View menuItemView = findViewById(R.id.reply_mail);
        PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.reply_mail_menu, popup.getMenu());
        popup.show();
    }

    /*
     *  Launch new ReplyActivity
     *  that sends an email back to
     *  "from" addresses
     */
    public void replyMail(MenuItem menu) {
        Intent intent = new Intent(this, ReplyActivity.class);
        intent.putExtra("fromEmail", Mailbox.account_email);
        intent.putExtra("password", Mailbox.account_password);
        intent.putExtra("subject","Re: "+ email.subject);
        intent.putExtra("body",""+Html.fromHtml(body_content).toString()); //DA INDENTARE
        intent.putExtra("to",from_addresses);
        intent.putExtra("replyType", "Reply");

        startActivity(intent);
    }
    /*
     *  Launch new Reply Activity from speech recognizer
     */
    public void replyMail(String content) {
        Intent intent = new Intent(this, ReplyActivity.class);
        intent.putExtra("fromEmail", Mailbox.account_email);
        intent.putExtra("password", Mailbox.account_password);
        intent.putExtra("subject","Re: "+ email.subject);
        intent.putExtra("body", content + " "+Html.fromHtml(body_content).toString()); //DA INDENTARE
        intent.putExtra("to",from_addresses);
        intent.putExtra("replyType", "Reply");

        startActivity(intent);
    }

    /*
    *  Launch new ReplyActivity
    *  that sends an email back to
    *  "from" addresses and to all "cc" ones
    */
    public void replyAll(MenuItem item) {
        Intent intent = new Intent(this, ReplyActivity.class);
        intent.putExtra("fromEmail", Mailbox.account_email);
        intent.putExtra("password", Mailbox.account_password);
        intent.putExtra("subject","Re: "+ email.subject);
        intent.putExtra("body",""+Html.fromHtml(body_content).toString()); //DA INDENTARE
        intent.putExtra("to",from_addresses);
        intent.putExtra("cc",cc_addresses);
        intent.putExtra("replyType", "ReplyAll");
        startActivity(intent);
    }

    /*
     *  Launch new Reply all activity
     *  from speech analyzer
     */
    public void replyAll(String content) {
        Intent intent = new Intent(this, ReplyActivity.class);
        intent.putExtra("fromEmail", Mailbox.account_email);
        intent.putExtra("password", Mailbox.account_password);
        intent.putExtra("subject","Re: "+ email.subject);
        intent.putExtra("body",content + " "+Html.fromHtml(body_content).toString()); //DA INDENTARE
        intent.putExtra("to",from_addresses);
        intent.putExtra("cc",cc_addresses);
        intent.putExtra("replyType", "ReplyAll");
        startActivity(intent);
    }

    /*
    *  Launch new ReplyActivity
    *  that forward an email
    */
    public void forwardMail(MenuItem item) {
        Intent intent = new Intent(this, ReplyActivity.class);
        intent.putExtra("fromEmail", Mailbox.account_email);
        intent.putExtra("password", Mailbox.account_password);
        intent.putExtra("subject","I: "+ email.subject);
        intent.putExtra("body",""+Html.fromHtml(body_content).toString()); //DA INDENTARE
        intent.putExtra("replyType", "Forward");
        startActivity(intent);
    }

    /*
     *  Sets the to do variable or unset it if the email is already pinned
     */
    public void setToDo(View view) {
        if (!call_from_sent) {
            if (email.todo) {
                email.removeTodo();
                todoButton.setBackgroundResource(R.drawable.not_pinned);
            } else {
                initiatePopupWindow();
            }
            Mailbox.save(Mailbox.emailList);
        }
    }

    public void deleteMail(View view) {
        if (!call_from_sent) {
            //find out which fragment is being displayed and remove from its list
            if (call_from_todo) {
                TodoFragment.todo_list.remove(email);
            }
            else if (call_from_inbox) {
                InboxFragment.inbox_email_list.remove(email);
            }
            Mailbox.emailList.get(mail_index).deleted = true;
            email.removeTodo();
            UpdateDeletedMailTask update_deleted_task = new UpdateDeletedMailTask(ReadMail.this, true);
            update_deleted_task.execute(email.ID);
            this.finish();
        }
    }

    /*
     *  Trigger speech recognition
     */
    public void speakButtonClicked(MenuItem menu) {

        startVoiceRecognitionActivity();
    }

    /*
     * Istantiate popup window and set click listeners
     * perform animation after pinning element
     */
    public void initiatePopupWindow() {
        /*
         *  Define popup layout
         */
        LayoutInflater inflater = (LayoutInflater) ReadMail.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup, (ViewGroup) ReadMail.this.findViewById(R.id.popup));
        popup = new PopupWindow(layout, 360, 400, true);
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAtLocation(layout, Gravity.CENTER, 0, -10);

        /*
         *  Set up popup_list and adapter
         */
        ListView popup_list = (ListView) layout.findViewById(R.id.popup_list);
        String[] popup_array = new String[]{"nessuna", "oggi", "domani", "scegli data"};
        ArrayAdapter<String> popup_Adapter = new ArrayAdapter<String>(ReadMail.this, R.layout.popup_element, popup_array);
            popup_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    switch (position){
                        /*
                         *  No date set for alarm (don't set an alarm)
                         */
                        case 0:
                            email.addTodo(null);
                            break;
                        /*
                         * Alarm set for today at end time
                         */
                        case 1:
                            GregorianCalendar today_date = new GregorianCalendar();
                            // Add email to todolist
                            email.addTodo(today_date);
                            break;
                        /*
                         * Alarm set for tomorrow at start time (reminder message)
                         * and tomorrow at end time (if not completed)
                         */
                        case 2:
                            GregorianCalendar tomorrow_date = new GregorianCalendar();
                            tomorrow_date.add(Calendar.DATE, 1);
                            email.addTodo(tomorrow_date);
                            break;
                        /*
                         *  Alarm set for a date's start time (reminder message)
                         *  and date's end time (if not completed)
                         */
                        case 3:
                            DialogFragment newFragment = new DatePickerFragment(){
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int day){
                                    GregorianCalendar expire_date = new GregorianCalendar(year, month, day);
                                    email.addTodo(expire_date);
                                }
                            };
                            newFragment.show(getFragmentManager(), "datePicker");
                            break;
                        default:
                            break;
                    }
                    todoButton.setBackgroundColor(R.color.yellow);
                    todoButton.setBackgroundResource(R.drawable.pinned);
                    popup.dismiss();
                }
            });
        popup_list.setAdapter(popup_Adapter);
    }

    /*
     *  Start speech recognition activity
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    /*
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            SpeechAnalyzer speechAnalysis = new SpeechAnalyzer();
            speechAnalysis.analyze(matches, ReadMail.this, email);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

