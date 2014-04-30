package com.example.mailclient.app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.mail.internet.InternetAddress;

/**
 * Created by Leo on 30/03/14.
 */

/*
 *  Custom adapter that displays two TextView in each row
 */
public class EmailAdapter extends ArrayAdapter<Email> implements View.OnTouchListener {

    //swipe variables
    public static enum Action {
        LR_TRIGGER,
        LR_BACK,
        RL_TRIGGER,
        RL_BACK,
        CLICK,
        RESET,
        REFRESH,
        None// when no action was detected
    }

    private TextView subjectView;
    private float downY, upX, upY;
    private Action mSwipeDetected = Action.None;
    private View cur_item;
    private float downX = 0;
    private float distanceX = 0;
    private float distanceY = 0;
    private float curX = 0;
    private float curY = 0;
    private int  move_count = 0;
    private float baseX = 0;
    private int screen_width, BLOCK_THRESHOLD, position;
    private boolean scrolling = false;



    private Context context;

    public EmailAdapter(Context context, int resource, ArrayList<Email> items) {
        super(context, resource,  items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.email_list, null);
        }

        Email item = getItem(position);
        if (item != null) {
            subjectView = (TextView) view.findViewById(R.id.list_subject);
            String subject_excerpt;
            if( item.subject.length() > 15 ) {
                subject_excerpt = item.subject.substring(0, 15);
                subject_excerpt += "...";
            }
            else {
                subject_excerpt = item.subject;
            }
            subjectView.setText(subject_excerpt);

            TextView fromView = (TextView) view.findViewById(R.id.list_from);
            String email = item.from == null ? null : ((InternetAddress) item.from[0]).getPersonal();
            if (email == null || email.isEmpty()) {
                email = item.from == null ? null : ((InternetAddress) item.from[0]).getAddress();
            }
            fromView.setText(email);

            TextView dateView = (TextView) view.findViewById(R.id.list_date);
            String date_format = new SimpleDateFormat("dd MMM").format(item.date.getTime());
            dateView.setText(date_format);

            TextView excerptView = (TextView) view.findViewById(R.id.list_excerpt);
            excerptView.setText(item.excerpt);

            /*
             *  Set bold if email not read
             */
            if (!item.seen) {
                subjectView.setTypeface(null, Typeface.BOLD);
                fromView.setTypeface(null, Typeface.BOLD);
                dateView.setTypeface(null, Typeface.BOLD);

                view.setBackgroundResource(R.drawable.unseen);
            }
            else {
                subjectView.setTypeface(null, Typeface.NORMAL);
                fromView.setTypeface(null, Typeface.NORMAL);
                dateView.setTypeface(null, Typeface.NORMAL);

                view.setBackgroundResource(R.drawable.seen);
            }
        }
        view.setOnTouchListener(this);
        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        processPosition(view);
        return false;
    }

    public void processPosition(View view) {

    }
}
