package com.javascouts.javascouting2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.javascouts.javascouting2.adapters.ActivityFragmentCommunication;
import com.javascouts.javascouting2.R;
import com.javascouts.javascouting2.room.Team;
import com.javascouts.javascouting2.room.UserDao;

import java.util.List;

public class AddTeamFragment extends Fragment {

    ActivityFragmentCommunication callback;
    private EditText numT, nameT, depotT, landerT;
    private CheckBox landB, sampleB, claimB, parkB, latchB, endParkB;
    private int num, depot, lander;
    private String name;
    private boolean canLand, canSample, canClaim, canPark, canLatch, canEndPark;
    private UserDao dao;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {

        Log.d("USER", "AddTeam Fragment: Detached");
        super.onDetach();
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
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        numT = view.findViewById(R.id.number);
        nameT = view.findViewById(R.id.name);
        depotT = view.findViewById(R.id.numDepot);
        landerT = view.findViewById(R.id.numLander);
        landB = view.findViewById(R.id.landBox);
        sampleB = view.findViewById(R.id.sampleBox);
        claimB = view.findViewById(R.id.claimBox);
        parkB = view.findViewById(R.id.parkBox);
        latchB = view.findViewById(R.id.latchBox);
        endParkB = view.findViewById(R.id.endParkBox);

    }

    public void getInfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                teams = dao.getAllTeams();
                Team tempTeam = new Team();

                try {

                    name = nameT.getText().toString();
                    num = Integer.valueOf(numT.getText().toString());
                    depot = Integer.valueOf(depotT.getText().toString());
                    lander = Integer.valueOf(landerT.getText().toString());

                } catch (NumberFormatException e) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getContext(), "All fields must be filled.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
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

                dao.insertTeams(tempTeam);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.findItem(R.id.cleanseMatches).setVisible(false);
        menu.findItem(R.id.cleanseTeams).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
        super.onCreateOptionsMenu(menu,menuInflater);
    }

}
