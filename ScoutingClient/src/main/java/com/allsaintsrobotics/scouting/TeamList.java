package com.allsaintsrobotics.scouting;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Iterator;

public class TeamList extends Activity implements Iterable<Team> {

    private ListView teamList;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamlist);

        teamList = (ListView) findViewById(R.id.teams);

        teamList.setAdapter(new TeamAdapter(this, R.layout.listitem_team, DatabaseManager.get().getTeams()));

        teamList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Go to the activities for viewing and editing
                Intent i = new Intent(TeamList.this, TeamDetail.class);

                Team t = (Team)teamList.getItemAtPosition(position);

                i.putExtra("team", t);

                startActivity(i);
            }
        });

        // Show the Up button in the action bar.
        setupActionBar();

        Toast.makeText(this, "Key: " + getIntent().getStringExtra("key"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_teamlist, menu);
        return true;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addteam:
                Intent i = new Intent(this, NewTeam.class);

                startActivityForResult(i, 0);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            Team t = data.getParcelableExtra("team");

            for (int i = 0; i < teamList.getAdapter().getCount(); i ++)
            {
                if (t.equals(teamList.getAdapter().getItem(i)))
                {
                    Toast.makeText(this, R.string.error_team_exists, Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // Will notify adapter of change.
            DatabaseManager.get().addTeam(t);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Iterator<Team> iterator() {
        return new ListViewIterator<Team>(teamList);
    }
}
