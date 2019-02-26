package com.javascouts.javascouting2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.javascouts.javascouting2.adapters.ActivityFragmentCommunication;
import com.javascouts.javascouting2.fragments.AnalysisFragment;
import com.javascouts.javascouting2.fragments.MatchesFragment;
import com.javascouts.javascouting2.fragments.ScoutingFragment;
import com.javascouts.javascouting2.room.TeamDatabase;
import com.javascouts.javascouting2.room.UserDao;

public class MainActivity extends AppCompatActivity implements ActivityFragmentCommunication {

    private String current;
    Fragment scoutingFragment, matchesFragment, analysisFragment;
    private UserDao dao;
    private TeamDatabase db;
    ActionBar bar;
    private final int REQUEST = 112;

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
                        analysisFragment = new AnalysisFragment();

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
            //bar.setDisplayHomeAsUpEnabled(true);
            //bar.setHomeButtonEnabled(true);
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST);
            }
        }

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
                    if(current.equals("ANALYSIS")){
                        return true;
                    }
                    FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                    transaction3.replace(R.id.fragHolder, analysisFragment);
                    transaction3.commit();
                    current = "ANALYSIS";
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
            case R.id.deleteMatch:
                return false;
            case R.id.deleteTeam:
                return false;
            case R.id.export:
                return false;
            case R.id.export2:
                return false;
            case R.id.inport:
                return false;
            case R.id.inport2:
                return false;
            case R.id.settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permissions were not granted!", Toast.LENGTH_LONG).show();
                }
            }
        }
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

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
