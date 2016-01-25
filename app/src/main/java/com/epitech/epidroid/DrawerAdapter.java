package com.epitech.epidroid;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerAdapter extends ArrayAdapter<String> {


    private Integer[] pictures = {
            null,
            R.drawable.ic_action_home_blue,
            R.drawable.ic_action_addcontact,
            R.drawable.ic_action_calendar_blue,
            R.drawable.ic_action_modules_blue,
            R.drawable.ic_action_projects_blue,
            R.drawable.ic_action_logout_blue
    };


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.drawer_row, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        textView.setText(getItem(position));

        if (pictures[position] != null)
            imageView.setImageResource(pictures[position]);

        return rowView;
    }

    public DrawerAdapter(Context context, String[] values) {
        super(context, R.layout.drawer_row, values);
    }
}
