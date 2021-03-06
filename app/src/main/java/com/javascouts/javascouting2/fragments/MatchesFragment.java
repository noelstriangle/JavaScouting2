package com.javascouts.javascouting2.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.javascouts.javascouting2.adapters.MatchAdapter;
import com.javascouts.javascouting2.room.Match;
import com.javascouts.javascouting2.room.ScoresTypeConverter;
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

public class MatchesFragment extends Fragment {

    ActivityFragmentCommunication callback;
    private Fragment addMatchFragment;
    private Fragment matchDetailsFragment;
    List<Match> matches;
    private UserDao dao;
    ListView list;
    private TeamDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_matches, parent, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                db = TeamDatabase.getTeamDatabase(getContext());
            }
        }).start();

        Log.d("USER", "Matches fragment: attached.");
        super.onAttach(context);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        addMatchFragment = new AddMatchFragment();
        matchDetailsFragment = new MatchDetailsFragment();

        list = view.findViewById(R.id.matchesList);

        com.robertlevonyan.views.customfloatingactionbutton.FloatingActionButton button = view.findViewById(R.id.matchesAdd);
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
//                                lv.setEmptyView(getActivity().findViewById(R.id.imageView2));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.findItem(R.id.cleanseTeams).setVisible(false);
        menu.findItem(R.id.deleteMatch).setVisible(false);
        menu.findItem(R.id.deleteTeam).setVisible(false);
        menu.findItem(R.id.export).setVisible(false);
        menu.findItem(R.id.inport).setVisible(false);
        super.onCreateOptionsMenu(menu,menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cleanseMatches:
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getContext());

                deleteBuilder.setTitle(R.string.delete_matches);

                deleteBuilder.setMessage(R.string.do_delete2);

                deleteBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                for (Match match : dao.getAllMatches()) {

                                    dao.deleteMatch(match);

                                }

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

            case R.id.export2:

                AlertDialog.Builder exportDialog = new AlertDialog.Builder(getContext());

                exportDialog.setTitle(R.string.export_matches);

                exportDialog.setMessage(R.string.do_export);

                exportDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "Exporting...", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                exportToDatabase();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(),"Done!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).start();

                    }
                });
                exportDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog exporter= exportDialog.create();
                exporter.show();

            case R.id.inport2:

                Toast.makeText(getContext(), "Please select an exported database!", Toast.LENGTH_LONG).show();
                doImport();

                return true;

            case R.id.settings:
                return false;
        }
        return true;
    }

    public boolean exportToDatabase() {

        String FILENAME = "matchDatabase.csv";
        File directoryDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File logDir = new File(directoryDownload, FILENAME);
        try {
            logDir.createNewFile();
            CSVWriter csvWriter = new CSVWriter(new FileWriter(logDir));
            Cursor curCSV = db.query("SELECT * FROM matches", null);
            csvWriter.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                String arrStr[] = {curCSV.getString(0) + " ", curCSV.getString(1) + " ",
                        curCSV.getString(2) + " ", curCSV.getString(3) + " ",
                        curCSV.getString(4) + " ", curCSV.getString(5) + " ",
                        curCSV.getString(6) + " "};
                csvWriter.writeNext(arrStr);
            }
            csvWriter.close();
            curCSV.close();
            return true;
        } catch (Exception e) {
            Log.e("exporting error:", e.getMessage(), e);
            return false;
        }


    }

    private void importFromDatabase(Uri input) {

        try {

            InputStream fileInputStream = Objects.requireNonNull(getContext()).getContentResolver().openInputStream(input);
            CSVParser parser = CSVParser.parse(Objects.requireNonNull(fileInputStream), Charset.defaultCharset(), CSVFormat.DEFAULT);
            List<CSVRecord> toAdd = parser.getRecords();
            final List<Match> toPush = new ArrayList<>();
            for (int i = 1; i < toAdd.size(); i++) {

                Match m = new Match();
                CSVRecord curr = toAdd.get(i);
                m.id = Integer.valueOf(curr.get(0).trim());
                m.matchNumber = Integer.valueOf(curr.get(1).trim());
                m.blue1 = Integer.valueOf(curr.get(2).trim());
                m.blue2 = Integer.valueOf(curr.get(3).trim());
                m.red1 = Integer.valueOf(curr.get(4).trim());
                m.red2 = Integer.valueOf(curr.get(5).trim());
                m.results = ScoresTypeConverter.toList(String.valueOf(curr.get(6).trim()));

                toPush.add(m);

            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Match m : toPush) {

                        dao.insertMatches(m);
                        Log.d("USER", "Match pushed: " + String.valueOf(m.matchNumber));

                    }
                }
            }).start();

        } catch (Exception e) {

            Log.e("USER", e.getMessage(), e);

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
