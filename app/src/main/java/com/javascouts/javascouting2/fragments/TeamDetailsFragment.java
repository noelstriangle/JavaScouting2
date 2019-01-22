package com.javascouts.javascouting2.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.javascouts.javascouting2.R;
import com.javascouts.javascouting2.adapters.ActivityFragmentCommunication;
import com.javascouts.javascouting2.room.Team;
import com.javascouts.javascouting2.room.TeamDao;

public class TeamDetailsFragment extends Fragment {

    ActivityFragmentCommunication callback;
    private TeamDao dao;
    private int id;
    Team team;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_teamdetails, parent, false);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("ID");
            Log.d("ID",Integer.valueOf(id).toString());
        }
    }

    @Override
    public void onAttach(Context context) {

        try {
            callback = (ActivityFragmentCommunication) context;
            dao = callback.getDao();

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ActivityFragmentCommuncation");
        }

        Log.d("USER", "Details fragment: attached.");
        super.onAttach(context);

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final TextView name = view.findViewById(R.id.detailName);
        final TextView number = view.findViewById(R.id.detailNum);
        final ConstraintLayout cl = view.findViewById(R.id.constraintLayout);
        final TableLayout tl = view.findViewById(R.id.teamTable);

        if (dao != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    team = dao.getTeamById(id);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(team != null) {
                                name.setText(team.teamName);
                                number.setText(Integer.valueOf(team.teamNumber).toString());
                                populateTable(team, tl);
                            }
                        }
                    });
                }
            }).start();
        } else {
            Log.d("USER","DAO is null.");
        }
    }

    @SuppressLint("SetTextI18n")
    void populateTable(Team team, TableLayout tl) {

        TextView tempq = new TextView(getContext());
        TextView tempa = new TextView(getContext());
        TableRow tempr = new TableRow(getContext());

        tempq.setText(R.string.question_land);
        tempa.setText(convertBooltoString(team.canLand));
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,0);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setText(R.string.question_sample);
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setText(convertBooltoString(team.canSample));
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,1);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setText(R.string.question_claim);
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setText(convertBooltoString(team.canClaim));
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,2);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setText(R.string.question_park);
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setText(convertBooltoString(team.canLand));
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,3);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setText(R.string.question_depot);
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setText(Integer.toString(team.depotMinerals));
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,4);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setText(R.string.question_lander);
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setText(Integer.toString(team.landerMinerals));
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,5);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setText(R.string.question_latch);
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setText(convertBooltoString(team.canLatch));
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,6);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setText(R.string.question_endpark);
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setText(convertBooltoString(team.canEndPark));
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,7);
        tl.setStretchAllColumns(true);
    }

    String convertBooltoString(boolean b) {
        if(b) {
            return getString(R.string.yes);
        } else {
            return getString(R.string.no);
        }
    }

}
