package com.example.mailclient.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Leo on 30/03/14.
 */

/*
 *  Custom adapter that displays two TextView in each row
 */
public class EmailAdapter extends ArrayAdapter<Email> {

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
            TextView subjectView = (TextView) view.findViewById(R.id.list_subject);
            subjectView.setText(item.subject);

            TextView fromView = (TextView) view.findViewById(R.id.list_from);
            fromView.setText(item.from[0].toString());

            TextView dateView = (TextView) view.findViewById(R.id.list_date);
            dateView.setText(item.date.toString());
        }
        return view;
    }
}
