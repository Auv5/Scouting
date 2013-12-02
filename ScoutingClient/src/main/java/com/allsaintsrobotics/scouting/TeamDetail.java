package com.allsaintsrobotics.scouting;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.allsaintsrobotics.scouting.adapters.MatchAdapter;
import com.allsaintsrobotics.scouting.adapters.QuestionAdapter;
import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.models.Team;
import com.allsaintsrobotics.scouting.survey.Question;

import java.util.List;

/**
 * Created by jack on 11/24/13.
 */
public class TeamDetail extends Activity {
    private static final String TAG = "TeamDetail";
    private Team team;

    private PitFragment pf;
    private MatchFragment mf;

    private boolean menuRender = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.team_detail);

        Intent i = getIntent();

        team = i.getParcelableExtra("team");


        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setTitle(String.format(getString(R.string.td_actionbar_title), team.getNumber()));

        ActionBar.Tab pitTab = actionBar.newTab().setText("Pit");
        ActionBar.Tab matchTab = actionBar.newTab().setText("Matches");

        pf = new PitFragment();
        mf = new MatchFragment();

        pitTab.setTabListener(new TeamDetail.DetailTabListener(pf));
        matchTab.setTabListener(new TeamDetail.DetailTabListener(mf));

        actionBar.addTab(pitTab);
        actionBar.addTab(matchTab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menuRender)
        {
            MenuInflater mi = this.getMenuInflater();
            mi.inflate(R.menu.pit_scout, menu);

            return true;
        }
        else
        {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_editscout:
                pf.openEditActivity();
                return true;
        }
        return false;
    }

    public class PitFragment extends Fragment {
        private ListView lv;

        private QuestionAdapter qa;

        public static final int REQUEST_EDIT_TEAM = 1;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pitdata, container, false);

            lv = (ListView) v.findViewById(R.id.pit_datalist);

            new QuestionPopulateTask().execute();

            return v;
        }

        public void populateQuestionList(List<Question> questions) {
            qa = new QuestionAdapter(TeamDetail.this, team, questions);
            lv.setAdapter(qa);
        }

        private class QuestionPopulateTask extends AsyncTask<Void, Void, List<Question>> {
            @Override
            protected List<Question> doInBackground(Void... params) {
                return ScoutingDBHelper.getInstance().getQuestions();
            }

            @Override
            protected void onPostExecute(List<Question> questions) {
                populateQuestionList(questions);
            }
        }

        public void openEditActivity() {
            Intent intent = new Intent();
            intent.setClass(TeamDetail.this, ScoutEdit.class);

            intent.putExtra("team", team);

            startActivityForResult(intent, REQUEST_EDIT_TEAM);
        }

        public void gotResult(Intent data) {
            // No actual results for now. Just tell the adapter to check for changes.
            qa.notifyDataSetChanged();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_EDIT_TEAM && resultCode == RESULT_OK) {
                gotResult(data);
            }
        }
    }

    public class MatchFragment extends Fragment {
        private static final int MATCH_DETAIL = 8001;
        private ListView lv;
        private TextView autoAverage;
        private TextView teleopAverage;
        private TextView specialAverage;
        private List<Match> matches;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_matchdata, container, false);

            this.lv = (ListView)v.findViewById(R.id.match_datalist);

            this.matches = ScoutingDBHelper.getInstance().getMatches(team);

            this.lv.setAdapter(new MatchAdapter(TeamDetail.this, team, matches));

            this.autoAverage = (TextView) v.findViewById(R.id.auto_average);
            this.teleopAverage = (TextView) v.findViewById(R.id.teleop_average);
            this.specialAverage = (TextView) v.findViewById(R.id.special_average);

            populateAverages();

            return v;
        }

        private void populateAverages() {
            int autoTotal = 0;
            int autoCount = 0;
            int teleopTotal = 0;
            int teleopCount = 0;
            int specialTotal = 0;
            int specialCount = 0;

            for (Match m : matches) {
                int auto = m.getAuto(team.getNumber());
                int teleop = m.getTeleop(team.getNumber());
                int special = m.getSpecial(team.getNumber());

                if (auto != -1) {
                    autoTotal += auto;
                    autoCount += 1;
                }

                if (teleop != -1) {
                    teleopTotal += teleop;
                    teleopCount += 1;
                }

                if (special != -1) {
                    specialTotal += special;
                    specialCount += 1;
                }
            }

            this.autoAverage.setText(String.format(getString(R.string.auto_average_format,
                    autoCount == 0 ? "None" : Integer.toString(autoTotal / autoCount))));
            this.teleopAverage.setText(String.format(getString(R.string.teleop_average_format),
                    teleopCount == 0 ? "None" : Integer.toString(teleopTotal / teleopCount)));
            this.specialAverage.setText(String.format(getString(R.string.special_average_format),
                    specialCount == 0 ? "None" : Integer.toString(specialTotal / specialCount)));
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == MATCH_DETAIL && resultCode == Activity.RESULT_OK) {
                populateAverages();
            }
        }
    }

    private class DetailTabListener implements ActionBar.TabListener {
        private Fragment fragment;

        public DetailTabListener(Fragment f) {
            this.fragment = f;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (fragment instanceof PitFragment) {
                menuRender = true;
            }
            else {
                menuRender = false;
            }

            TeamDetail.this.invalidateOptionsMenu();

            ft.replace(R.id.detail_fragcont, fragment);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }
    }
}
