package com.javascouts.javascouting2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TeamAdapter extends ArrayAdapter<Team> {

    TeamAdapter(Context context, int textViewResourceId) {

        super(context, textViewResourceId);

    }

    TeamAdapter(Context context, int resource, List<Team> items) {

        super(context, resource, items);

    }


    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.content_teamrow, null);

        }

        Team p = getItem(position);

        if (p != null) {
            TextView tt1 = v.findViewById(R.id.titleText);
            TextView tt2 = v.findViewById(R.id.subTitleText);
            TextView tt3 = v.findViewById(R.id.pointsText);

            /*v.setBackgroundColor(Color.rgb(37, 130, 41));
            tt1.setTextColor(Color.rgb(255, 202, 43));
            tt2.setTextColor(Color.rgb(255,202,43));
            tt3.setTextColor(Color.rgb(255,202,43));*/


            if (tt1 != null) {
                tt1.setText(p.teamName);
            }

            if (tt2 != null) {
                tt2.setText(String.valueOf(p.teamNumber));
            }

            if (tt3 != null) {
                tt3.setText(String.valueOf(p.telePoints + p.autoPoints));
            }
        }

        return v;
    }

}
