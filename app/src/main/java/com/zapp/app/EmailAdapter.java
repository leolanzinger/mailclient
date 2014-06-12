package com.zapp.app;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mailclient.app.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.mail.internet.InternetAddress;

/**
 * Created by Leo on 30/03/14.
 */

/*
 *  Custom adapter that displays Emails in list
 */
public class EmailAdapter extends ArrayAdapter<Email> implements View.OnTouchListener {

    Fragment sent_fragment, todo_fragment;
    private Context context;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    public EmailAdapter(Context context, int resource, ArrayList<Email> items) {
        super(context, resource,  items);
        this.context = context;

        /*
         *  We use those fragments to check in which FragmentActivity
         *  we are working on!
         */
        sent_fragment = ((MainActivity) context).getFragmentManager().findFragmentByTag("SENT");
        todo_fragment = ((MainActivity) context).getFragmentManager().findFragmentByTag("TODO");
    }


    @Override
    public int getItemViewType(int position) {
        if(!getItem(position).separator){
            return TYPE_ITEM;
        }
        else {
            return TYPE_SEPARATOR;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        Email item = getItem(position);
        int rowType = getItemViewType(position);

//        if (view == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (rowType) {
            case TYPE_ITEM:
                view = inflater.inflate(R.layout.email_list_item, null);
                break;
            case TYPE_SEPARATOR:
                view = inflater.inflate(R.layout.todo_separator, null);
                break;
        }
//        }

        if (item != null && rowType == TYPE_ITEM) {
            TextView subjectView = (TextView) view.findViewById(R.id.list_subject);
            TextView fromView = (TextView) view.findViewById(R.id.list_from);
            TextView dateView = (TextView) view.findViewById(R.id.list_date);
            TextView excerptView = (TextView) view.findViewById(R.id.list_excerpt);

            /*
             * Put Email subject excerpt instead of full subject
             * to avoid long text cropping.
             */
            String subject_excerpt;
            /*
             *  Check if there is no subject
             */

            if (item.subject == null) {
                subject_excerpt = "(nessun oggetto)";
                subject_excerpt += "...";

            }
            else {
                if (item.subject.length() > 21) {
                    subject_excerpt = item.subject.substring(0, 20);
                    subject_excerpt += "..";
                } else {
                    subject_excerpt = item.subject;
                }
            }
            subjectView.setText(subject_excerpt);

            /*
             *  Put recipient instead of send address
             *  if we are in SentFragment, otherwise
             *  put send address.
             */
            String email;
            if (sent_fragment != null && sent_fragment.isVisible()) {
                email = item.to == null ? null : ((InternetAddress) item.to[0]).getPersonal();
                if (email == null || email.isEmpty()) {
                    email = item.to == null ? null : ((InternetAddress) item.to[0]).getAddress();
                }
            }
            else {
                email = item.from == null ? null : ((InternetAddress) item.from[0]).getPersonal();
                if (email == null || email.isEmpty()) {
                    email = item.from == null ? null : ((InternetAddress) item.from[0]).getAddress();
                }
            }
            fromView.setText(email);

            /*
             *  Put expire date if we are in TodoFragment
             *  otherwise put Email sent date.
             */
            String date_format;
            if (todo_fragment != null && todo_fragment.isVisible()) {
                if (item.expire_date != null) {
                    date_format = "";
                }
                else {
                    date_format = new SimpleDateFormat("dd MMM").format(item.date.getTime());
                }
            }
            else {
                date_format = new SimpleDateFormat("dd MMM").format(item.date.getTime());
            }
            dateView.setText(date_format);

            /*
             *  Set excerpt from Email
             */
            excerptView.setText(Html.fromHtml(item.excerpt));

            /*
             *  Set bold if email not read
             */
            if (!item.seen) {
                subjectView.setTypeface(null, Typeface.BOLD);
                fromView.setTypeface(null, Typeface.BOLD);
                dateView.setTypeface(null, Typeface.BOLD);
                View frontground = view.findViewById(R.id.list_content);
                frontground.setBackgroundResource(R.drawable.unseen);
            } else {
                subjectView.setTypeface(null, Typeface.NORMAL);
                fromView.setTypeface(null, Typeface.NORMAL);
                dateView.setTypeface(null, Typeface.NORMAL);
                View frontground = view.findViewById(R.id.list_content);
                GregorianCalendar gregorio = new GregorianCalendar();
                if(item.expire_date != null && item.expire_date.get(GregorianCalendar.DAY_OF_YEAR) == gregorio.get(GregorianCalendar.DAY_OF_YEAR)) {
                    applyDifferentBackgroundResource(R.drawable.seen_expire,frontground);
                } else {
                    applyDifferentBackgroundResource(R.drawable.seen,frontground);
                }
            }
        }
        else if (item != null && rowType == TYPE_SEPARATOR) {
            TextView subjectView = (TextView) view.findViewById(R.id.separator_title);
            subjectView.setText(item.subject);
        }

        view.setOnTouchListener(this);
        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        processPosition(view);
        return false;
    }

    /*
     * Override this method from caller Fragment to
     * retrieve position of the View in the List.
     */
    public void processPosition(View view) {

    }

    private void applyDifferentBackgroundResource(int resId, View v){
        final int paddingBottom = v.getPaddingBottom();
        final int paddingLeft   = v.getPaddingLeft();
        final int paddingRight  = v.getPaddingRight();
        final int paddingTop    = v.getPaddingTop();
        v.setBackgroundResource(resId);
        v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }
}
