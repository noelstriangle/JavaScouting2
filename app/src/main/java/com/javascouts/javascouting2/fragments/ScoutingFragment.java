package com.javascouts.javascouting2.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.javascouts.javascouting2.R;
import com.javascouts.javascouting2.adapters.ActivityFragmentCommunication;
import com.javascouts.javascouting2.adapters.TeamAdapter;
import com.javascouts.javascouting2.room.Team;
import com.javascouts.javascouting2.room.TeamDatabase;
import com.javascouts.javascouting2.room.UserDao;
import com.opencsv.CSVWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScoutingFragment extends Fragment {

    private ActivityFragmentCommunication callback;
    private Fragment addTeamFragment;
    private Fragment teamDetailsFragment;
    private List<Team> teams;
    private UserDao dao;
    private ListView list;
    private TeamDatabase db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scouting, parent, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (ActivityFragmentCommunication) context;
            dao = callback.getDao();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    db = TeamDatabase.getTeamDatabase(getContext());
                }
            }).start();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ActivityFragmentCommuncation");
        }
        Log.d("USER", "Scouting fragment: attached.");

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addTeamFragment = new AddTeamFragment();
        teamDetailsFragment = new TeamDetailsFragment();

        list = view.findViewById(R.id.scoutingList);

        com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton button = view.findViewById(R.id.scoutingAdd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.setCurrent("ADD_TEAM");
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = null;
                if (fm != null) {
                    ft = fm.beginTransaction();
                }
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
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshList(getContext(), list);

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
//                                lv.setEmptyView(getActivity().findViewById(R.id.imageView));
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        FragmentManager fm = getFragmentManager();
                                        FragmentTransaction ft = null;
                                        if (fm != null) {
                                            ft = fm.beginTransaction();
                                        }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.findItem(R.id.cleanseMatches).setVisible(false);
        menu.findItem(R.id.deleteMatch).setVisible(false);
        menu.findItem(R.id.deleteTeam).setVisible(false);
        menu.findItem(R.id.export2).setVisible(false);
        menu.findItem(R.id.inport2).setVisible(false);
        super.onCreateOptionsMenu(menu,menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.cleanseTeams:
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getContext());

                deleteBuilder.setTitle(R.string.delete_header);

                deleteBuilder.setMessage(R.string.do_delete);

                deleteBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                for (Team team : dao.getAllTeams()) {

                                    dao.deleteTeam(team);

                                }

                                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Objects.requireNonNull(getActivity()).recreate();
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

                return true;

            case R.id.export:

                AlertDialog.Builder exportDialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()));

                exportDialog.setTitle(R.string.export_teams);

                exportDialog.setMessage(R.string.do_export);

                exportDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Exporting...", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                if (exportToDatabase()) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), "Done!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }
                        }).start();

                    }
                });
                exportDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog exporter = exportDialog.create();
                exporter.show();
                return true;

            case R.id.inport:

                doImport();

                return true;

            default:

                return super.onOptionsItemSelected(item);
        }

    }


    public boolean exportToDatabase() {

        String FILENAME = "teamDatabase.csv";
        File directoryDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File logDir = new File(directoryDownload, FILENAME);
        try {
            logDir.createNewFile();
            CSVWriter csvWriter = new CSVWriter(new FileWriter(logDir));
            Cursor curCSV = db.query("SELECT * FROM teams", null);
            csvWriter.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                String arrStr[] = {curCSV.getString(0) + " ", curCSV.getString(1) + " ",
                        curCSV.getString(2) + " ", curCSV.getString(3) + " ",
                        curCSV.getString(4) + " ", curCSV.getString(5) + " ",
                        curCSV.getString(6) + " ", curCSV.getString(7) + " ",
                        curCSV.getString(8) + " ", curCSV.getString(9) + " ",
                        curCSV.getString(10) + " ", curCSV.getString(11) + " ",
                        curCSV.getString(12) + " ", curCSV.getString(13) + " ",
                        curCSV.getString(14) + " "};
                csvWriter.writeNext(arrStr);
            }
            csvWriter.close();
            curCSV.close();
            return true;
        } catch (Exception e) {
            Looper.prepare();
            Toast.makeText(getContext(), "Exporting failed!", Toast.LENGTH_SHORT).show();
            Log.e("exporting error:", e.getMessage(), e);
            return false;
        }
    }

    private void importFromDatabase(Uri input) {

        try {

            InputStream fileInputStream = Objects.requireNonNull(getContext()).getContentResolver().openInputStream(input);
            CSVParser parser = CSVParser.parse(Objects.requireNonNull(fileInputStream), Charset.defaultCharset(), CSVFormat.DEFAULT);
            List<CSVRecord> toAdd = parser.getRecords();
            final List<Team> toPush = new ArrayList<>();
            for (int i = 1; i < toAdd.size(); i++) {

                Team t = new Team();
                CSVRecord curr = toAdd.get(i);
                t.id = Integer.valueOf(curr.get(0).trim());
                t.teamName = curr.get(1).trim();
                t.teamNumber = Integer.valueOf(curr.get(2).trim());
                t.canLand = Boolean.valueOf(curr.get(3).trim());
                t.canSample = Boolean.valueOf(curr.get(4).trim());
                t.canClaim = Boolean.valueOf(curr.get(5).trim());
                t.canPark = Boolean.valueOf(curr.get(6).trim());
                t.depotMinerals = Integer.valueOf(curr.get(7).trim());
                t.landerMinerals = Integer.valueOf(curr.get(8).trim());
                t.canLatch = Boolean.valueOf(curr.get(9).trim());
                t.canEndPark = Boolean.valueOf(curr.get(10).trim());
                t.autoPoints = Integer.valueOf(curr.get(11).trim());
                t.telePoints = Integer.valueOf(curr.get(12).trim());
                t.endPoints = Integer.valueOf(curr.get(13).trim());
                t.standardDeviation = Integer.valueOf(curr.get(14).trim());
                toPush.add(t);

            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Team t : toPush) {

                        dao.insertTeams(t);
                        Log.d("USER", "Team pushed: " + String.valueOf(t.teamNumber));

                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().recreate();
                        }
                    });
                }
            }).start();


        } catch (Exception e){

            Log.e("USER",e.getMessage(),e);

        }

    }

    private static final int READ_REQUEST_CODE = 42;

    public void doImport() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.setType("text/*");

        startActivityForResult(intent, READ_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                importFromDatabase(uri);
            }
        }
    }

}
