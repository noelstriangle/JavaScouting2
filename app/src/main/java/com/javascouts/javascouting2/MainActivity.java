package com.javascouts.javascouting2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.javascouts.javascouting2.adapters.ActivityFragmentCommunication;
import com.javascouts.javascouting2.fragments.MatchesFragment;
import com.javascouts.javascouting2.fragments.ScoutingFragment;
import com.javascouts.javascouting2.room.UserDao;
import com.javascouts.javascouting2.room.TeamDatabase;

public class MainActivity extends AppCompatActivity implements ActivityFragmentCommunication {

    private String current;
    Fragment scoutingFragment, matchesFragment;
    private UserDao dao;
    private TeamDatabase db;
    ActionBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                db = TeamDatabase.getTeamDatabase(getApplicationContext());
                dao = db.getTeamDao();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scoutingFragment = new ScoutingFragment();
                        matchesFragment = new MatchesFragment();

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
                    db = TeamDatabase.getTeamDatabase(getApplicationContext());
                    dao = db.getTeamDao();
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
                    transaction2.replace(R.id.fragHolder, matchesFragment);
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
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cleanseTeams:
                return false;
            case R.id.cleanseMatches:
                return false;
            case R.id.settings:
                break;
        }
        return true;
    }

    @Override
    public UserDao getDao() {
        return dao;
    }
    @Override
    public void setDao(UserDao dao) {
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
