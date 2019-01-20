package com.javascouts.javascouting2;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Liam on 1/17/2019.
 */

public class ScoutingFragment extends Fragment {

    ActivityFragmentCommunication callback;
    private Fragment addTeamFragment;
    List<Team> teams;
    private TeamDao dao;
    ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_scouting, parent, false);

    }

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

        Log.d("USER", "Scouting fragment: attached.");

    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d("USER", "Scouting fragment: detached.");

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        addTeamFragment = new AddTeamFragment();

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

        list = view.findViewById(R.id.scoutingList);

        getActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);

        refreshList(getContext(), list);

    }

    @Override
    public void onStart() {
        super.onStart();

        cleanse();

        refreshList(getContext(), list);

    }

    public void cleanse() {

        if (teams != null) {
            for (Team team : teams) {

                dao.delete(team);

            }
        }

    }

    public void refreshList(final Context context, final ListView lv) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                teams = dao.getAllSortByTeamNumber();
                Log.d("USER", "Teams: " + String.valueOf(teams.size()));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (teams != null) {
                            TeamAdapter ta = new TeamAdapter(context, R.layout.content_teamrow, teams);
                            lv.setAdapter(ta);
                            lv.setEmptyView(getActivity().findViewById(R.id.imageView));
                        }
                    }
                });
            }
        }).start();

    }

}
