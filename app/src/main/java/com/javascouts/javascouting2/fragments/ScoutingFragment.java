package com.javascouts.javascouting2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.javascouts.javascouting2.adapters.ActivityFragmentCommunication;
import com.javascouts.javascouting2.R;
import com.javascouts.javascouting2.room.Team;
import com.javascouts.javascouting2.adapters.TeamAdapter;
import com.javascouts.javascouting2.room.TeamDao;

import java.util.List;

public class ScoutingFragment extends Fragment {

    ActivityFragmentCommunication callback;
    private Fragment addTeamFragment;
    private Fragment teamDetailsFragment;
    List<Team> teams;
    private TeamDao dao;
    ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scouting, parent, false);
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


        Log.d("USER", "Scouting fragment: attached.");
        super.onAttach(context);

    }

    @Override
    public void onDetach() {

        Log.d("USER", "Scouting fragment: detached.");
        super.onDetach();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        addTeamFragment = new AddTeamFragment();
        teamDetailsFragment = new TeamDetailsFragment();

        list = view.findViewById(R.id.scoutingList);
        refreshList(getContext(), list);

        FloatingActionButton button = view.findViewById(R.id.scoutingAdd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.setCurrent("ADD_TEAM");
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragHolder, addTeamFragment);
                ft.addToBackStack("add");
                ft.commit();

            }
        });

        getActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);

        final SwipeRefreshLayout srl = view.findViewById(R.id.refreshTeams);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList(getContext(), list);
                srl.setRefreshing(false);
            }
        });

        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onResume() {
        //cleanse();

        refreshList(getContext(), list);
        super.onResume();

    }

    public void refreshList(final Context context, final ListView lv) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dao != null) {
                    teams = dao.getAllSortByTeamNumber();
                    Log.d("USER", "Teams: " + String.valueOf(teams.size()));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (teams != null) {
                                TeamAdapter ta = new TeamAdapter(context, R.layout.content_teamrow, teams);
                                lv.setAdapter(ta);
                                lv.setEmptyView(getActivity().findViewById(R.id.imageView));
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        FragmentManager fm = getFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        Bundle b = new Bundle();
                                        b.putInt("ID",teams.get(i).id);
                                        teamDetailsFragment.setArguments(b);
                                        ft.replace(R.id.fragHolder, teamDetailsFragment);
                                        ft.addToBackStack("details");
                                        ft.commit();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }).start();

    }

}
