package com.javascouts.javascouting2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.javascouts.javascouting2.R;
import com.javascouts.javascouting2.room.Ana;

import java.util.List;

public class AnalysisAdapter extends ArrayAdapter<Ana> {

    public AnalysisAdapter(Context context, int textViewResourceId) {

        super(context, textViewResourceId);

    }

    public AnalysisAdapter(Context context, int resource, List<Ana> items) {

        super(context, resource, items);

    }


    @NonNull
    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.content_analrow, null);

        }

        Ana p = getItem(position);

        if (p != null) {
            TextView tt1 = v.findViewById(R.id.titleTexta);
            //TextView tt2 = v.findViewById(R.id.subTitleText);
            TextView tt3 = v.findViewById(R.id.numTexta);

            /*v.setBackgroundColor(Color.rgb(37, 130, 41));
            tt1.setTextColor(Color.rgb(255, 202, 43));
            tt2.setTextColor(Color.rgb(255,202,43));
            tt3.setTextColor(Color.rgb(255,202,43));*/

            if (tt1 != null) {
                tt1.setText("Score: " + String.valueOf(Math.round(p.expectedPerformance * 100.0) / 100.0));
            }

            if (tt3 != null) {
                tt3.setText("Team: " + String.valueOf(p.teamNum));
            }

        }

        return v;
    }

}
