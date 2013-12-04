package com.allsaintsrobotics.scouting;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.allsaintsrobotics.scouting.adapters.TeamAdapter;
import com.allsaintsrobotics.scouting.models.Team;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by jack on 11/24/13.
 */
public class TeamList extends Fragment {
    private ListView teamList;
    private List<Team> teams;
    private TeamAdapter adapter;
    private Context context;

    public TeamList(Context c) {
        this.context = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.team_list, container, false);

        this.teamList = (ListView) v.findViewById(R.id.lv_teams);

        if (!ScoutingDBHelper.isInitialized()) {
            new DatabaseSetupTask().execute();
        }
        else {
            populateTeamList();
        }

        this.teamList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.setClass(context, TeamDetail.class);

                i.putExtra("team", teams.get(position));

                startActivity(i);
            }
        });

        return v;
    }

    private void populateTeamList() {
        this.teams = ScoutingDBHelper.getInstance().getTeams();

        this.adapter = new TeamAdapter(context, teams);

        teamList.setAdapter(adapter);
    }

    public void invalidate() {
        this.adapter.notifyDataSetChanged();
    }

    private class DatabaseSetupTask extends AsyncTask<Void, Void, ScoutingDBHelper> {
        @Override
        protected ScoutingDBHelper doInBackground(Void... params) {
            return ScoutingDBHelper.makeInstance(context);
        }

        protected void onPostExecute(ScoutingDBHelper result) {
            populateTeamList();
        }
    }
}