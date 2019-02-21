package com.javascouts.javascouting2.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.javascouts.javascouting2.R;
import com.javascouts.javascouting2.adapters.ActivityFragmentCommunication;
import com.javascouts.javascouting2.adapters.AnalysisAdapter;
import com.javascouts.javascouting2.room.Ana;
import com.javascouts.javascouting2.room.Team;
import com.javascouts.javascouting2.room.UserDao;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AnalysisFragment extends Fragment {

    private static final String MODEL_PATH = "adam.tflite";
    private Interpreter tflite;
    ActivityFragmentCommunication callback;
    UserDao dao;
    ListView list;
    List<Team> teams;
    List<Ana> allAnalysis;
    Activity a;
    Button b;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis, parent, false);
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
                    teams = dao.getAllTeams();
                }
            }).start();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ActivityFragmentCommuncation");
        }
        Log.d("USER", "Analysis fragment: attached.");

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = view.findViewById(R.id.anaList);

        b = view.findViewById(R.id.startA);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData(list, teams);
            }
        });

        a = getActivity();

        /*final SwipeRefreshLayout srl = view.findViewById(R.id.refreshTeams);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);

    }

    public void refreshData(ListView list, List<Team> teams) {

        if (teams.size() != 0) {

            try {
                tflite = new Interpreter(loadModelFile(a));
            } catch (IOException e) {
                Log.d("USER", "IO exception at line 85-95");
            }

            Log.d("USER", "Data is being refreshed. Teams: length " + String.valueOf(teams.size()));

            allAnalysis = new ArrayList<>();

            for (Team team : teams) {

                float[][] o = new float[1][1];
                o[0][0] = 0;

                float[] input = new float[]{team.autoPoints, team.telePoints, team.endPoints, team.autoPoints + team.telePoints + team.endPoints, team.standardDeviation};
                tflite.run(input, o);
                Log.d("USER", "prediction for " + String.valueOf(team.teamNumber) + " = " + String.valueOf(o[0][0]));

                allAnalysis.add(new Ana(team.teamNumber, o[0][0]));

            }

            tflite.close();

            b.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                allAnalysis.sort(new Comparator<Ana>() {
                    @Override
                    public int compare(Ana o1, Ana o2) {
                        return Float.compare(o2.expectedPerformance, o1.expectedPerformance);
                    }
                });
            } else {
                new Toast(getContext()).makeText(getContext(), R.string.ff, Toast.LENGTH_SHORT).show();
            }
            AnalysisAdapter ta = new AnalysisAdapter(getContext(), R.layout.content_teamrow, allAnalysis);
            list.setAdapter(ta);

        } else {
            new Toast(getContext()).makeText(getContext(), R.string.add_some_teams, Toast.LENGTH_SHORT).show();
        }
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {

        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.findItem(R.id.cleanseMatches).setVisible(false);
        menu.findItem(R.id.cleanseTeams).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
        menu.findItem(R.id.deleteMatch).setVisible(false);
        menu.findItem(R.id.deleteTeam).setVisible(false);
        menu.findItem(R.id.export).setVisible(false);
        menu.findItem(R.id.export2).setVisible(false);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

}
