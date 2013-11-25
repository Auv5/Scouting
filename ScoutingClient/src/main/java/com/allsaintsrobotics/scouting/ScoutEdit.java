package com.allsaintsrobotics.scouting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.allsaintsrobotics.scouting.models.Team;

/**
 * Created by jack on 11/25/13.
 */
public class ScoutEdit extends Activity {
    private Team team;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scout_edit);

        Intent i = getIntent();

        this.team = i.getParcelableExtra("team");

        getActionBar().setTitle(String.format(getString(R.string.se_actionbar_title), team.getNumber()));
    }

    @Override
    public void onBackPressed() {
//        Intent returnIntent = new Intent();
//
//        returnIntent.putExtra("team", team);
//
//        setResult(Activity.RESULT_CANCELED, returnIntent);
//
//        finish();
    }
}