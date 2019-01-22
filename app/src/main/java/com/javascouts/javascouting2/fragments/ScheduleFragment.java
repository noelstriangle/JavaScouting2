package com.javascouts.javascouting2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.javascouts.javascouting2.R;

public class ScheduleFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_schedule, parent, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }
}
