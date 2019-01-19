package com.javascouts.javascouting2;

import android.arch.persistence.room.Room;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ActivityFragmentCommunication {

    String current;
    Fragment scoutingFragment, scheduleFragment;
    private TeamDao dao;
    public TeamDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setTitle(R.string.title);
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        scoutingFragment = new ScoutingFragment();
        scheduleFragment = new ScheduleFragment();

        fragmentTransaction.replace(R.id.fragHolder, scoutingFragment);

        fragmentTransaction.commit();
        current = "SCOUTING";

        new Thread(new Runnable() {
            @Override
            public void run() {
                db = Room.databaseBuilder(getApplicationContext(),
                        TeamDatabase.class, "team-database").build();
                dao = db.teamDao();
            }
        }).start();

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
}
