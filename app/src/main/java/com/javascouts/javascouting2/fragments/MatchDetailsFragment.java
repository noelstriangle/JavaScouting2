package com.javascouts.javascouting2.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.javascouts.javascouting2.R;
import com.javascouts.javascouting2.adapters.ActivityFragmentCommunication;
import com.javascouts.javascouting2.room.Match;
import com.javascouts.javascouting2.room.Team;
import com.javascouts.javascouting2.room.UserDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatchDetailsFragment extends Fragment {

    ActivityFragmentCommunication callback;
    private UserDao dao;
    private int id;
    Team r1, r2, b1, b2;
    Match match;
    TextView number;
    TableLayout tl, pl;
    EditText ra, rt, re, ba, bt, be;
    Button save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_matchdetails, parent, false);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("ID");
            Log.d("ID", Integer.valueOf(id).toString());
        }
        setHasOptionsMenu(true);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (dao != null) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    match = dao.getMatchById(id);
                    Log.d("USER", "Match number received: " + String.valueOf(match.matchNumber));
                    r1 = dao.getTeamByTeamNumber(match.red1);
                    r2 = dao.getTeamByTeamNumber(match.red2);
                    b1 = dao.getTeamByTeamNumber(match.blue1);
                    b2 = dao.getTeamByTeamNumber(match.blue2);
                    final List<Integer> r = match.results;
                    if (r.get(0) != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ra.setText(String.valueOf(r.get(0)));
                                rt.setText(String.valueOf(r.get(1)));
                                re.setText(String.valueOf(r.get(2)));
                                ba.setText(String.valueOf(r.get(3)));
                                bt.setText(String.valueOf(r.get(4)));
                                be.setText(String.valueOf(r.get(5)));
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                number.setText(String.valueOf(match.matchNumber));
                                populateTable(tl, r1, r2, b1, b2);
                                TableRow row = new TableRow(getContext());

                                TextView tempView = new TextView(getContext());
                                tempView.setText(String.valueOf(sumPoints(r1) + sumPoints(r2)));
                                tempView.setTextSize(22);
                                tempView.setBackground(getActivity().getDrawable(R.drawable.line_divider));
                                tempView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                tempView.setTextColor(getResources().getColor(R.color.red));
                                row.addView(tempView, 0);

                                tempView = new TextView(getContext());
                                tempView.setText(String.valueOf(sumPoints(b1) + sumPoints(b2)));
                                tempView.setTextSize(22);
                                tempView.setBackground(getActivity().getDrawable(R.drawable.line_divider));
                                tempView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                tempView.setTextColor(getResources().getColor(R.color.blue));
                                row.addView(tempView, 1);

                                pl.addView(row);
                                pl.setStretchAllColumns(true);

                            } catch (NullPointerException e) {

                                Toast toast = new Toast(getContext()).makeText(getContext(), "One or more teams don't exist.", Toast.LENGTH_SHORT);
                                toast.show();
                                FragmentManager fm = getFragmentManager();
                                fm.popBackStack();
                                e.printStackTrace();

                            }

                        }
                    });

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Integer[] t0 = new Integer[6];

                            try {
                                t0[0] = Integer.valueOf(ra.getText().toString());
                                t0[1] = Integer.valueOf(rt.getText().toString());
                                t0[2] = Integer.valueOf(re.getText().toString());
                                t0[3] = Integer.valueOf(ba.getText().toString());
                                t0[4] = Integer.valueOf(bt.getText().toString());
                                t0[5] = Integer.valueOf(be.getText().toString());
                                if (!match.updated) {
                                    r1.standardDeviation = r1.standardDeviation + ((t0[0] / 2) + (t0[1] / 2) + (t0[2] / 2)) - (r1.autoPoints + r1.telePoints + r1.endPoints);
                                    r1.autoPoints = (r1.autoPoints + t0[0]) / 2;
                                    r1.telePoints = (r1.telePoints + t0[1]) / 2;
                                    r1.endPoints = (r1.endPoints + t0[2]) / 2;

                                    r2.standardDeviation = r2.standardDeviation + ((t0[0] / 2) + (t0[1] / 2) + (t0[2] / 2)) - (r2.autoPoints + r2.telePoints + r2.endPoints);
                                    r2.autoPoints = (r2.autoPoints + t0[0]) / 2;
                                    r2.telePoints = (r2.telePoints + t0[1]) / 2;
                                    r2.endPoints = (r2.endPoints + t0[2]) / 2;

                                    b1.standardDeviation = b1.standardDeviation + ((t0[3] / 2) + (t0[4] / 2) + (t0[5] / 2)) - (b1.autoPoints + b1.telePoints + b1.endPoints);
                                    b1.autoPoints = (b1.autoPoints + t0[3]) / 2;
                                    b1.telePoints = (b1.telePoints + t0[4]) / 2;
                                    b1.endPoints = (b1.endPoints + t0[5]) / 2;

                                    b2.standardDeviation = b2.standardDeviation + ((t0[3] / 2) + (t0[4] / 2) + (t0[5] / 2)) - (b2.autoPoints + b2.telePoints + b2.endPoints);
                                    b2.autoPoints = (b2.autoPoints + t0[3]) / 2;
                                    b2.telePoints = (b2.telePoints + t0[4]) / 2;
                                    b2.endPoints = (b2.endPoints + t0[5]) / 2;
                                    match.updated = true;
                                }

                            } catch (NumberFormatException e) {

                                Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                                return;

                            }
                            final List<Integer> t = new ArrayList<>(Arrays.asList(t0));

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    match.results = t;
                                    dao.updateMatch(match);
                                    dao.updateTeams(r1, r2, b1, b2);
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

                }
            }).start();
        } else {
            Log.d("USER", "DAO is null.");
        }

    }

    private int sumPoints(Team t) {

        return t.autoPoints + t.telePoints + t.endPoints;

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
        pl = view.findViewById(R.id.predictionTable);
        save = view.findViewById(R.id.save);
        ra = view.findViewById(R.id.autoResultr);
        ba = view.findViewById(R.id.autoResultb);
        rt = view.findViewById(R.id.teleResultr);
        bt = view.findViewById(R.id.teleResultb);
        re = view.findViewById(R.id.endResultr);
        be = view.findViewById(R.id.endResultb);

    }


    private TableRow createRow(Team r1, Team r2, Team b1, Team b2, String id) {

        TableRow temp = new TableRow(getContext());

        TextView tempView = new TextView(getContext());
        tempView.setText(id);
        tempView.setTextSize(22);
        tempView.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        temp.addView(tempView, 0);

        tempView = new TextView(getContext());
        tempView.setText(getInfo(id, r1), 0, getInfo(id, r1).length);
        tempView.setTextSize(22);
        tempView.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempView.setTextColor(getResources().getColor(R.color.red));
        temp.addView(tempView, 1);

        tempView = new TextView(getContext());
        tempView.setText(getInfo(id, r2), 0, getInfo(id, r2).length);
        tempView.setTextSize(22);
        tempView.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempView.setTextColor(getResources().getColor(R.color.red));
        temp.addView(tempView, 2);

        tempView = new TextView(getContext());
        tempView.setText(getInfo(id, b1), 0, getInfo(id, b1).length);
        tempView.setTextSize(22);
        tempView.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempView.setTextColor(getResources().getColor(R.color.blue));
        temp.addView(tempView, 3);

        tempView = new TextView(getContext());
        tempView.setText(getInfo(id, b2), 0, getInfo(id, b2).length);
        tempView.setTextSize(22);
        tempView.setBackground(getActivity().getDrawable(R.drawable.line_divider));
        tempView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tempView.setTextColor(getResources().getColor(R.color.blue));
        temp.addView(tempView, 4);

        return temp;

    }


    private void populateTable(final TableLayout tl, Team r1, Team r2, Team b1, Team b2) {

        try {

            tl.addView(createRow(r1, r2, b1, b2, "NUMBER"), 0);
            tl.addView(createRow(r1, r2, b1, b2, "LAND"), 1);
            tl.addView(createRow(r1, r2, b1, b2, "SAMPLE"), 2);
            tl.addView(createRow(r1, r2, b1, b2, "CLAIM"), 3);
            tl.addView(createRow(r1, r2, b1, b2, "NUM DEPOT"), 4);
            tl.addView(createRow(r1, r2, b1, b2, "NUM LANDER"), 5);
            tl.addView(createRow(r1, r2, b1, b2, "PARK"), 6);
            tl.addView(createRow(r1, r2, b1, b2, "LATCH"), 7);
            tl.setStretchAllColumns(true);

        } catch (NullPointerException e) {

            Toast toast = new Toast(getContext()).makeText(getContext(), "One or more teams don't exist.", Toast.LENGTH_SHORT);
            toast.show();
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
            e.printStackTrace();

        }
    }

    public char[] getInfo(String id, Team team) {

        if (id.equals("NUMBER")) {
            return String.valueOf(team.teamNumber).toCharArray();
        }
        if (id.equals("LAND")) {
            if (team.canLand) {
                char[] temp = new char[1];
                temp[0] = '\u2713';
                return temp;
            } else {
                char[] temp = new char[1];
                temp[0] = '\u2717';
                return temp;
            }
        }
        if (id.equals("SAMPLE")) {
            if (team.canSample) {
                char[] temp = new char[1];
                temp[0] = '\u2713';
                return temp;
            } else {
                char[] temp = new char[1];
                temp[0] = '\u2717';
                return temp;
            }
        }
        if (id.equals("CLAIM")) {
            if (team.canClaim) {
                char[] temp = new char[1];
                temp[0] = '\u2713';
                return temp;
            } else {
                char[] temp = new char[1];
                temp[0] = '\u2717';
                return temp;
            }
        }
        if (id.equals("NUM DEPOT")) {
            return String.valueOf(team.depotMinerals).toCharArray();
        }
        if (id.equals("NUM LANDER")) {
            return String.valueOf(team.landerMinerals).toCharArray();
        }
        if (id.equals("PARK")) {
            if (team.canPark) {
                char[] temp = new char[1];
                temp[0] = '\u2713';
                return temp;
            } else {
                char[] temp = new char[1];
                temp[0] = '\u2717';
                return temp;
            }
        }
        if (id.equals("LATCH")) {
            if (team.canLatch) {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.findItem(R.id.cleanseMatches).setVisible(false);
        menu.findItem(R.id.cleanseTeams).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
        menu.findItem(R.id.deleteTeam).setVisible(false);
        menu.findItem(R.id.export).setVisible(false);
        menu.findItem(R.id.export2).setVisible(false);
        menu.findItem(R.id.inport).setVisible(false);
        menu.findItem(R.id.inport2).setVisible(false);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.deleteMatch:

                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getContext());

                deleteBuilder.setTitle(R.string.delete_header3);

                deleteBuilder.setMessage(R.string.do_delete34);

                deleteBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                dao.deleteMatch(match);

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
