package com.javascouts.javascouting2;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.List;

/**
 * Created by Liam on 1/17/2019.
 */

public class AddTeamFragment extends Fragment {

    ActivityFragmentCommunication callback;
    private EditText numT, nameT, depotT, landerT;
    private CheckBox landB, sampleB, claimB, parkB, latchB, endParkB;
    private int num, depot, lander;
    private String name;
    private boolean canLand, canSample, canClaim, canPark, canLatch, canEndPark;
    private TeamDao dao;
    private List<Team> teams;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (ActivityFragmentCommunication) context;
            dao = callback.getDao();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ActivityFragmentCommuncation");
        }

        Log.d("USER", "AddTeam Fragment: Attached");

    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d("USER", "AddTeam Fragment: Detached");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_addteam, parent, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        Button finish = view.findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getInfo();

            }
        });

        getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);

        numT = view.findViewById(R.id.number);
        nameT = view.findViewById(R.id.name);
        depotT = view.findViewById(R.id.numDepot);
        landerT = view.findViewById(R.id.numLander);
        landB = view.findViewById(R.id.landBox);
        sampleB = view.findViewById(R.id.sampleBox);
        claimB = view.findViewById(R.id.claimBox);
        parkB = view.findViewById(R.id.parkBox);
        latchB = view.findViewById(R.id.latchBox);
        latchB.setVisibility(View.INVISIBLE);
        endParkB = view.findViewById(R.id.endParkBox);
        landB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    latchB.setVisibility(View.VISIBLE);
                    endParkB.setVisibility(View.INVISIBLE);
                } else {
                    latchB.setVisibility(View.INVISIBLE);
                    endParkB.setVisibility(View.VISIBLE);
                }
            }
        });
        latchB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    endParkB.setVisibility(View.INVISIBLE);
                } else {
                    endParkB.setVisibility(View.VISIBLE);
                }
            }
        });
        endParkB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    latchB.setVisibility(View.INVISIBLE);
                    landB.setVisibility(View.INVISIBLE);
                } else {
                    latchB.setVisibility(View.VISIBLE);
                    landB.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    public void getInfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                teams = dao.getAll();
                Team tempTeam = new Team();

                try {

                    name = nameT.getText().toString();
                    num = Integer.valueOf(numT.getText().toString());
                    depot = Integer.valueOf(depotT.getText().toString());
                    lander = Integer.valueOf(landerT.getText().toString());

                } catch (NumberFormatException e) {

                    //TODO: add team number unspecified message
                    return;

                }

                if (teams.contains(dao.getTeamByTeamNumber(num))) {

                    //TODO: add team number already exists message, or make auto-fill for an already completed team
                    return;

                }

                canLand = landB.isChecked();
                canClaim = claimB.isChecked();
                canSample = sampleB.isChecked();
                canPark = parkB.isChecked();
                canLatch = latchB.isChecked();
                canEndPark = endParkB.isChecked();

                tempTeam.teamName = name;
                tempTeam.teamNumber = num;

                tempTeam.canLand = canLand;
                tempTeam.canClaim = canClaim;
                tempTeam.canSample = canSample;
                tempTeam.canPark = canPark;
                tempTeam.canLatch = canLatch;
                tempTeam.canEndPark = canEndPark;

                int points = 0;
                if (canLand) {
                    points = points + 30;
                }
                if (canSample) {
                    points = points + 25;
                }
                if (canClaim) {
                    points = points + 15;
                }
                if (canPark) {
                    points = points + 10;
                }
                tempTeam.autoPoints = points;

                points = (depot * 2) + (lander * 5);
                if (canLatch) {
                    points = points + 50;
                }
                if (canEndPark) {
                    points = points + 25;
                }
                tempTeam.telePoints = points;

                dao.insertAll(tempTeam);
                Log.d("USER", "Team added: " + name);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FragmentManager fm = getFragmentManager();
                        fm.popBackStack();
                    }
                });

            }
        }).start();

    }

}
