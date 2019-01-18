package com.javascouts.javascouting2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Liam on 1/17/2019.
 */

public class ScoutingFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_scouting, parent, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }
}
