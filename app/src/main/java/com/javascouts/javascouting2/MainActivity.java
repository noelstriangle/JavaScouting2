package com.javascouts.javascouting2;

import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ActivityFragmentCommunication {

    private String current;
    Fragment scoutingFragment, scheduleFragment;
    AlertDialog dialog;
    private TeamDao dao;
    private TeamDatabase db;
    ActionBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                db = Room.databaseBuilder(getApplicationContext(),
                        TeamDatabase.class, "team-database")
                        .fallbackToDestructiveMigration()
                        .build();
                dao = db.teamDao();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scoutingFragment = new ScoutingFragment();
                        scheduleFragment = new ScheduleFragment();

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        fragmentTransaction.replace(R.id.fragHolder, scoutingFragment);

                        fragmentTransaction.commit();
                        current = "SCOUTING";
                    }
                });
            }
        }).start();

        bar = getSupportActionBar();

        if (bar != null) {
            bar.setTitle(R.string.title);
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if ((db == null) || (dao == null)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    db = Room.databaseBuilder(getApplicationContext(),
                            TeamDatabase.class, "team-database").build();
                    dao = db.teamDao();
                }
            }).start();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_scouting:
                    if(current.equals("SCOUTING")){
                        return true;
                    }
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragHolder, scoutingFragment);
                    transaction.commit();
                    current = "SCOUTING";

                    return true;
                case R.id.navigation_schedule:
                    if(current.equals("SCHEDULE")){
                        return true;
                    }
                    FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                    transaction2.replace(R.id.fragHolder, scheduleFragment);
                    transaction2.commit();
                    current = "SCHEDULE";
                    return true;
                case R.id.navigation_analysis:

                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scouting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cleanse:
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);

                deleteBuilder.setTitle(R.string.delete_header);

                deleteBuilder.setMessage(R.string.do_delete);

                deleteBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                for (Team team : dao.getAll()) {

                                    dao.delete(team);

                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        recreate();
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

            case R.id.settings:

                break;

        }
        return true;
    }




    @Override
    public TeamDao getDao() {
        return dao;
    }
    @Override
    public void setDao(TeamDao dao) {
        this.dao = dao;
    }
    @Override
    public TeamDatabase getDb() {
        return db;
    }
    @Override
    public void setDb(TeamDatabase db) {
        this.db = db;
    }
    @Override
    public String getCurrent() {
        return current;
    }
    @Override
    public void setCurrent(String current) {
        this.current = current;
    }

}
