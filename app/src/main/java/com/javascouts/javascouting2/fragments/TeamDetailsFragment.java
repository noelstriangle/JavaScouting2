package com.javascouts.javascouting2.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.javascouts.javascouting2.R;
import com.javascouts.javascouting2.adapters.ActivityFragmentCommunication;
import com.javascouts.javascouting2.room.Team;
import com.javascouts.javascouting2.room.UserDao;

public class TeamDetailsFragment extends Fragment {

    ActivityFragmentCommunication callback;
    private UserDao dao;
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
        setHasOptionsMenu(true);
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

        tempq.setTextSize(22);
        tempa.setTextSize(22);
        tempq.setText(R.string.question_land);
        tempq.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempa.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempa.setText(convertBooltoString(team.canLand));
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,0);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setTextSize(22);
        tempa.setTextSize(22);
        tempq.setText(R.string.question_sample);
        tempq.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempa.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setText(convertBooltoString(team.canSample));
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,1);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setTextSize(22);
        tempa.setTextSize(22);
        tempq.setText(R.string.question_claim);
        tempq.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempa.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setText(convertBooltoString(team.canClaim));
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,2);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setTextSize(22);
        tempa.setTextSize(22);
        tempq.setText(R.string.question_park);
        tempq.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempa.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setText(convertBooltoString(team.canLand));
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,3);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setTextSize(22);
        tempa.setTextSize(22);
        tempq.setText(R.string.question_depot);
        tempq.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempa.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setText(String.valueOf(team.depotMinerals));
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,4);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setTextSize(22);
        tempa.setTextSize(22);
        tempq.setText(R.string.question_lander);
        tempq.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempa.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setText(String.valueOf(team.landerMinerals));
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,5);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setTextSize(22);
        tempa.setTextSize(22);
        tempq.setText(R.string.question_latch);
        tempq.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempa.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setText(convertBooltoString(team.canLatch));
        tempr.addView(tempq);
        tempr.addView(tempa);
        tl.addView(tempr,6);
        tempr = new TableRow(getContext());
        tempq = new TextView(getContext());
        tempa = new TextView(getContext());
        tempq.setTextSize(22);
        tempa.setTextSize(22);
        tempq.setText(R.string.question_endpark);
        tempq.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempa.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempq.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempa.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.findItem(R.id.cleanseMatches).setVisible(false);
        menu.findItem(R.id.cleanseTeams).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
        menu.findItem(R.id.deleteMatch).setVisible(false);
        menu.findItem(R.id.export).setVisible(false);
        menu.findItem(R.id.export2).setVisible(false);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.deleteTeam:

                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getContext());

                deleteBuilder.setTitle(R.string.delete_header2);

                deleteBuilder.setMessage(R.string.do_delete23);

                deleteBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                dao.deleteTeam(team);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getFragmentManager().popBackStack();
                                    }
                                });
                            }
                        }).start();

                    }
                });
                deleteBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog deleteDialog = deleteBuilder.create();
                deleteDialog.show();

                break;

        }

        return true;

    }

}
