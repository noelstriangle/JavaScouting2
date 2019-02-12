package com.javascouts.javascouting2.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.javascouts.javascouting2.R;
import com.javascouts.javascouting2.adapters.ActivityFragmentCommunication;
import com.javascouts.javascouting2.room.Match;
import com.javascouts.javascouting2.room.Team;
import com.javascouts.javascouting2.room.UserDao;

public class MatchDetailsFragment extends Fragment {

    ActivityFragmentCommunication callback;
    private UserDao dao;
    private int id;
    Team r1, r2, b1, b2;
    Match match;
    TextView number;
    TableLayout tl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_matchdetails, parent, false);

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

        number = view.findViewById(R.id.number);
        tl = view.findViewById(R.id.matchTable);

        if (dao != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    match = dao.getMatchById(id);
                    Log.d("USER","Match number received: "+String.valueOf(match.matchNumber));
                    r1 = dao.getTeamByTeamNumber(match.red1);
                    r2 = dao.getTeamByTeamNumber(match.red2);
                    b1 = dao.getTeamByTeamNumber(match.blue1);
                    b2 = dao.getTeamByTeamNumber(match.blue2);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                number.setText(String.valueOf(match.matchNumber));
                                populateTable(tl,r1,r2,b1,b2);
                        }
                    });
                }
            }).start();
        } else {
            Log.d("USER","DAO is null.");
        }

    }

    private TableRow createRow(Team r1, Team r2, Team b1, Team b2, String id) {

        TableRow temp = new TableRow(getContext());

        TextView tempView = new TextView(getContext());
        tempView.setText(id);
        tempView.setTextSize(22);
        tempView.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        temp.addView(tempView,0);

        tempView = new TextView(getContext());
        tempView.setText(getInfo(id,r1),0,getInfo(id,r1).length);
        tempView.setTextSize(22);
        tempView.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempView.setTextColor(getResources().getColor(R.color.red));
        temp.addView(tempView,1);

        tempView = new TextView(getContext());
        tempView.setText(getInfo(id,r2),0,getInfo(id,r2).length);
        tempView.setTextSize(22);
        tempView.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempView.setTextColor(getResources().getColor(R.color.red));
        temp.addView(tempView,2);

        tempView = new TextView(getContext());
        tempView.setText(getInfo(id,b1),0,getInfo(id,b1).length);
        tempView.setTextSize(22);
        tempView.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempView.setTextColor(getResources().getColor(R.color.blue));
        temp.addView(tempView,3);

        tempView = new TextView(getContext());
        tempView.setText(getInfo(id,b2),0,getInfo(id,b2).length);
        tempView.setTextSize(22);
        tempView.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempView.setTextColor(getResources().getColor(R.color.blue));
        temp.addView(tempView,4);

        return temp;

    }


    private void populateTable(final TableLayout tl, Team r1, Team r2, Team b1, Team b2) {

        try {

            tl.addView(createRow(r1,r2,b1,b2,"NUMBER"),0);
            tl.addView(createRow(r1,r2,b1,b2,"LAND"),1);
            tl.addView(createRow(r1,r2,b1,b2,"SAMPLE"),2);
            tl.addView(createRow(r1,r2,b1,b2,"CLAIM"),3);
            tl.addView(createRow(r1,r2,b1,b2,"NUM DEPOT"),4);
            tl.addView(createRow(r1,r2,b1,b2,"NUM LANDER"),5);
            tl.addView(createRow(r1,r2,b1,b2,"PARK"),6);
            tl.addView(createRow(r1,r2,b1,b2,"LATCH"),7);
            tl.setStretchAllColumns(true);

        } catch(NullPointerException e) {

            Toast toast = new Toast(getContext()).makeText(getContext(), "One or more teams don't exist.", Toast.LENGTH_SHORT);
            toast.show();
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
            e.printStackTrace();

        }
    }

    public char[] getInfo(String id, Team team) {

        if(id.equals("NUMBER")) {
            return String.valueOf(team.teamNumber).toCharArray();
        }
        if(id.equals("LAND")) {
            if(team.canLand) {
                char[] temp = new char[1];
                temp[0] = '\u2713';
                return temp;
            } else {
                char[] temp = new char[1];
                temp[0] = '\u2717';
                return temp;
            }
        }
        if(id.equals("LAND")) {
            if(team.canLand) {
                char[] temp = new char[1];
                temp[0] = '\u2713';
                return temp;
            } else {
                char[] temp = new char[1];
                temp[0] = '\u2717';
                return temp;
            }
        }
        if(id.equals("SAMPLE")) {
            if(team.canSample) {
                char[] temp = new char[1];
                temp[0] = '\u2713';
                return temp;
            } else {
                char[] temp = new char[1];
                temp[0] = '\u2717';
                return temp;
            }
        }
        if(id.equals("CLAIM")) {
            if(team.canClaim) {
                char[] temp = new char[1];
                temp[0] = '\u2713';
                return temp;
            } else {
                char[] temp = new char[1];
                temp[0] = '\u2717';
                return temp;
            }
        }
        if(id.equals("NUM DEPOT")) {
            return String.valueOf(team.depotMinerals).toCharArray();
        }
        if(id.equals("NUM LANDER")) {
            return String.valueOf(team.landerMinerals).toCharArray();
        }
        if(id.equals("PARK")) {
            if(team.canPark) {
                char[] temp = new char[1];
                temp[0] = '\u2713';
                return temp;
            } else {
                char[] temp = new char[1];
                temp[0] = '\u2717';
                return temp;
            }
        }
        if(id.equals("LATCH")) {
            if(team.canLatch) {
                char[] temp = new char[1];
                temp[0] = '\u2713';
                return temp;
            } else {
                char[] temp = new char[1];
                temp[0] = '\u2717';
                return temp;
            }
        }
        return new char[]{'P'};
    }

}
