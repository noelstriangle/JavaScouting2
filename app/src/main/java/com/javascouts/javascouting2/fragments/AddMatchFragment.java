package com.javascouts.javascouting2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.javascouts.javascouting2.R;
import com.javascouts.javascouting2.adapters.ActivityFragmentCommunication;
import com.javascouts.javascouting2.room.Match;
import com.javascouts.javascouting2.room.Team;
import com.javascouts.javascouting2.room.UserDao;

import java.util.ArrayList;
import java.util.List;

public class AddMatchFragment extends Fragment {

    ActivityFragmentCommunication callback;
    private UserDao dao;
    private List<Team> teams;
    private List<Match> matches;
    private int num, numberOfTeams;
    private Integer[] teamNums;
    private int b1,b2,r1,r2;
    private Spinner bs1,bs2,rs1,rs2;
    private EditText editNum;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (ActivityFragmentCommunication) context;
            dao = callback.getDao();
            startInfo();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ActivityFragmentCommuncation");
        }

        Log.d("USER", "AddMatch Fragment: Attached");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onDetach() {

        Log.d("USER", "AddMatch Fragment: Detached");
        super.onDetach();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_addmatch, parent, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        Button finish = view.findViewById(R.id.finishMatch);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getInfo();

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);

    }


    private void startInfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                teams = new ArrayList<>();

                teams = dao.getAllSortByTeamNumber();
                numberOfTeams = teams.size();

                matches = new ArrayList<>();
                matches = dao.getAllMatchesSortByMatchNumber();

                teamNums = new Integer[teams.size()];

                Log.d("RESUMING", "Arrays Created.");

                for(int i = 0; i < numberOfTeams; i++) {

                    Team tempTeam = teams.get(i);

                    teamNums[i] = tempTeam.teamNumber;

                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        startUi(teamNums);

                    }
                });

            }
        }).start();
    }

    private void startUi(Integer[] teamNumbers) {

        bs1 = getActivity().findViewById(R.id.b1Spinner);
        bs2 = getActivity().findViewById(R.id.b2Spinner);
        rs1 = getActivity().findViewById(R.id.r1Spinner);
        rs2 = getActivity().findViewById(R.id.r2Spinner);
        editNum = getActivity().findViewById(R.id.matchNumberInput);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, teamNums);

        bs1.setAdapter(adapter);
        bs1.setGravity(Gravity.CENTER);
        bs2.setAdapter(adapter);
        bs2.setGravity(Gravity.CENTER);
        rs1.setAdapter(adapter);
        rs1.setGravity(Gravity.CENTER);
        rs2.setAdapter(adapter);
        rs2.setGravity(Gravity.CENTER);

    }

    private void getInfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Match tempMatch = new Match();

                b1 = Integer.valueOf(bs1.getSelectedItem().toString());
                b2 = Integer.valueOf(bs2.getSelectedItem().toString());
                r1 = Integer.valueOf(rs1.getSelectedItem().toString());
                r2 = Integer.valueOf(rs2.getSelectedItem().toString());

                try {

                    num = Integer.valueOf(editNum.getText().toString());
                    tempMatch.matchNumber = num;
                    tempMatch.blue1 = b1;
                    tempMatch.blue2 = b2;
                    tempMatch.red1 = r1;
                    tempMatch.red2 = r2;
                    tempMatch.updated = false;
                    tempMatch.results = new ArrayList<>();

                } catch(NumberFormatException e) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast t = new Toast(getContext()).makeText(getContext(), "One or more fields are not filled out", Toast.LENGTH_SHORT);
                            t.show();
                        }
                    });

                    return;

                }

                if(matches.contains(dao.getMatchByMatchNumber(num))) {

                    //TODO add pre-filled in matches

                }

                dao.insertMatches(tempMatch);

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
        menu.findItem(R.id.deleteMatch).setVisible(false);
        menu.findItem(R.id.deleteTeam).setVisible(false);
        menu.findItem(R.id.export).setVisible(false);
        menu.findItem(R.id.export2).setVisible(false);
        menu.findItem(R.id.inport).setVisible(false);
        menu.findItem(R.id.inport2).setVisible(false);
        super.onCreateOptionsMenu(menu,menuInflater);
    }

}
