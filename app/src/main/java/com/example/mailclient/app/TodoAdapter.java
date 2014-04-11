package com.example.mailclient.app;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.mail.internet.InternetAddress;

/**
 * Created by Leo on 30/03/14.
 */

/*
 *  Custom adapter that displays only to do emails
 */
public class TodoAdapter extends BaseAdapter {

    private ArrayList<Email> todo_list = new ArrayList<Email>();

    private Context context;

    public TodoAdapter(Context context) {
        this.context = context;
    }

    public void updateTodo (ArrayList<Email> todo) {
        ThreadPreconditions.checkOnMainThread();
        this.todo_list = todo;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return todo_list.size();
    }

    @Override
    public Email getItem(int i) {
        return todo_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.email_list, parent, false);
        }
//
        Email item = getItem(position);
            if (item.todo == true) {
                TextView subjectView = (TextView) view.findViewById(R.id.list_subject);
                String subject_excerpt;
                if (item.subject.length() > 15) {
                    subject_excerpt = item.subject.substring(0, 15);
                    subject_excerpt += "...";
                } else {
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
                } else {
                    subjectView.setTypeface(null, Typeface.NORMAL);
                    fromView.setTypeface(null, Typeface.NORMAL);
                    dateView.setTypeface(null, Typeface.NORMAL);

                    view.setBackgroundResource(R.drawable.seen);
                }
            }
            else {
                view.setVisibility(View.GONE);
            }
        return view;
    }

}

