package com.allsaintsrobotics.scouting;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.allsaintsrobotics.scouting.adapters.TeamAdapter;
import com.allsaintsrobotics.scouting.models.Team;

/**
 * Created by jack on 11/24/13.
 */
public class TeamList extends Activity {
    ListView teamList;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_list);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        this.teamList = (ListView) findViewById(R.id.lv_teams);

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
                i.setClass(TeamList.this, TeamDetail.class);

                i.putExtra("team", (Team)view.getTag());

                startActivity(i);
            }
        });
    }

    private void populateTeamList() {
        teamList.setAdapter(new TeamAdapter(TeamList.this,
                ScoutingDBHelper.getInstance().getAllTeams(ScoutingDBHelper.TEAM_NUM)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = this.getMenuInflater();
        mi.inflate(R.menu.team_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_opensettings:
                Intent i = new Intent();
                i.setClass(this, Preferences.class);

                startActivityForResult(i, 0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DatabaseSetupTask extends AsyncTask<Void, Void, ScoutingDBHelper>
    {
        @Override
        protected ScoutingDBHelper doInBackground(Void... params) {
            return ScoutingDBHelper.makeInstance(TeamList.this);
        }

        protected void onPostExecute(ScoutingDBHelper result) {
            populateTeamList();
        }
    }
}