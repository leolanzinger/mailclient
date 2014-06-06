package com.example.mailclient.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Leo on 30/03/14.
 */

/*
 *  Adapter that displays the layout of the left menu drawer
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
        icon.setImageDrawable(context.getResources().getDrawable(context.getResources().getIdentifier(item, "drawable", context.getPackageName())));

        TextView title = (TextView) view.findViewById(R.id.drawer_list_title);
        String[] titles = context.getResources().getStringArray(R.array.sidebar_titles);
        title.setText(titles[position]);

        return view;
    }
}
