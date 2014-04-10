package com.example.mailclient.app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.mail.internet.InternetAddress;

/**
 * Created by Leo on 30/03/14.
 */

/*
 *  Custom adapter that displays two TextView in each row
 */
public class DrawerAdapter extends ArrayAdapter<String> {

    private Context context;

    public DrawerAdapter(Context context, int resource, String[] items) {
        super(context, resource,  items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_list, null);
        }

        String item = getItem(position);
        ImageView icon = (ImageView) view.findViewById(R.id.drawer_list_image);
        icon.setImageResource(g);

        TextView title = (TextView) view.findViewById(R.id.drawer_list_title);
        String[] titles = context.getResources().getStringArray(R.array.sidebar_titles);
        title.setText(titles[position]);

        return view;
    }
}
