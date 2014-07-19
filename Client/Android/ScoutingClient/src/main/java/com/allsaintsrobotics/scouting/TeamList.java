package com.allsaintsrobotics.scouting;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.allsaintsrobotics.scouting.adapters.TeamAdapter;
import com.allsaintsrobotics.scouting.models.Team;

import java.util.List;

/**
 * Created by jack on 11/24/13.
 * This file is a part of the ASTECHZ Scouting Client.
 */
public class TeamList extends Fragment {
    private ListView teamList;
    private List<Team> teams;
    private TeamAdapter adapter;

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
                i.setClass(getActivity(), TeamDetail.class);

                i.putExtra("team", teams.get(position));

                startActivity(i);
            }
        });

        return v;
    }

    private void populateTeamList() {
        this.teams = ScoutingDBHelper.getInstance().getTeams();

        this.adapter = new TeamAdapter(getActivity(), teams);

        teamList.setAdapter(adapter);
    }

    public void invalidate() {
        this.adapter.notifyDataSetChanged();
    }

    private class DatabaseSetupTask extends AsyncTask<Void, Void, ScoutingDBHelper> {
        @Override
        protected ScoutingDBHelper doInBackground(Void... params) {
            return ScoutingDBHelper.makeInstance(getActivity());
        }

        protected void onPostExecute(ScoutingDBHelper result) {
            populateTeamList();
        }
    }
}