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
import android.widget.Toast;

import com.javascouts.javascouting2.R;
import com.javascouts.javascouting2.adapters.ActivityFragmentCommunication;
import com.javascouts.javascouting2.adapters.MatchAdapter;
import com.javascouts.javascouting2.adapters.TeamAdapter;
import com.javascouts.javascouting2.room.Match;
import com.javascouts.javascouting2.room.Team;
import com.javascouts.javascouting2.room.UserDao;

import java.util.List;

public class MatchesFragment extends Fragment {

    ActivityFragmentCommunication callback;
    private Fragment addMatchFragment;
    private Fragment matchDetailsFragment;
    List<Match> matches;
    private UserDao dao;
    ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_matches, parent, false);
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

        addMatchFragment = new AddMatchFragment();
        matchDetailsFragment = new MatchDetailsFragment();

        list = view.findViewById(R.id.matchesList);
        refreshList(getContext(), list);

        FloatingActionButton button = view.findViewById(R.id.matchesAdd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(dao.getAllTeams().size() > 3) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.setCurrent("ADD_MATCH");
                                    FragmentManager fm = getFragmentManager();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.replace(R.id.fragHolder, addMatchFragment);
                                    ft.addToBackStack("add_match");
                                    ft.commit();
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast t1 = new Toast(getContext()).makeText(getContext(), R.string.add_1_pleasae, Toast.LENGTH_SHORT);
                                    t1.show();

                                }
                            });
                         }
                    }
                }).start();
            }
        });

        getActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);

        final SwipeRefreshLayout srl = view.findViewById(R.id.refreshMatches);
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
                    matches = dao.getAllMatchesSortByMatchNumber();
                    Log.d("USER", "Matches: " + String.valueOf(matches.size()));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (matches != null) {
                                MatchAdapter ma = new MatchAdapter(context, R.layout.content_teamrow, matches);
                                lv.setAdapter(ma);
                                lv.setEmptyView(getActivity().findViewById(R.id.imageView));
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        FragmentManager fm = getFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        Bundle b = new Bundle();
                                        b.putInt("ID",matches.get(i).id);
                                        matchDetailsFragment.setArguments(b);
                                        ft.replace(R.id.fragHolder, matchDetailsFragment);
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
