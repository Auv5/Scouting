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

        pf = PitFragment.getInstance(team);
        mf = MatchFragment.getInstance(team);

        pitTab.setTabListener(new TeamDetail.DetailTabListener(pf));
        matchTab.setTabListener(new TeamDetail.DetailTabListener(mf));

        actionBar.addTab(pitTab);
        actionBar.addTab(matchTab);

        if (savedInstanceState != null && savedInstanceState.containsKey("tabstate")) {
            actionBar.setSelectedNavigationItem(
                    savedInstanceState.getInt("tabstate"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tabstate", getActionBar().getSelectedNavigationIndex());
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

    public static class PitFragment extends Fragment {
        private ListView lv;

        private QuestionAdapter qa;

        private Team team;

        private Activity activity;

        public static final int REQUEST_EDIT_TEAM = 1;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pitdata, container, false);

            lv = (ListView) v.findViewById(R.id.pit_datalist);

            if (savedInstanceState == null) {
                team = getArguments().getParcelable("team");
            }
            else {
                team = savedInstanceState.getParcelable("team");
            }

            return v;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putParcelable("team", team);

            super.onSaveInstanceState(outState);
        }

        public static PitFragment getInstance(Team team) {
            PitFragment pf = new PitFragment();

            Bundle b = new Bundle();

            b.putParcelable("team", team);

            pf.setArguments(b);

            return pf;
        }

        @Override
        public void onAttach(Activity activity) {
            this.activity = activity;

            new QuestionPopulateTask().execute();

            super.onAttach(activity);
        }

        public void populateQuestionList(List<Question> questions) {
            qa = new QuestionAdapter(this.activity, team, questions);
            lv.setAdapter(qa);
            qa.notifyDataSetChanged();
        }

        private class QuestionPopulateTask extends AsyncTask<Void, Void, List<Question>> {
            @Override
            protected List<Question> doInBackground(Void... params) {
                List<Question> questions = ScoutingDBHelper.getInstance().getQuestions();

                return questions;
            }

            @Override
            protected void onPostExecute(List<Question> questions) {
                populateQuestionList(questions);
            }
        }

        public void openEditActivity() {
            Intent intent = new Intent();
            intent.setClass(getActivity(), ScoutEdit.class);

            intent.putExtra("team", team);

            startActivityForResult(intent, REQUEST_EDIT_TEAM);
        }

        public void gotResult(Intent data) {
            new QuestionPopulateTask().execute();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_EDIT_TEAM && resultCode == RESULT_OK) {
                gotResult(data);
            }
        }
    }

    public static class MatchFragment extends Fragment {
        private static final int MATCH_DETAIL = 8001;
        private ListView lv;
        private TextView autoAverage;
        private TextView teleopAverage;
        private TextView specialAverage;
        private List<Match> matches;
        private Team team;
        private Activity activity;

        public static MatchFragment getInstance(Team t) {
            MatchFragment mf = new MatchFragment();

//
            Bundle b = new Bundle();
            b.putParcelable("team", t);

            mf.setArguments(b);

            return mf;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putParcelable("team", team);
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onAttach(Activity activity) {
            this.activity = activity;

            super.onAttach(activity);

            if (matches != null) {
                populateAverages();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_matchdata, container, false);

            this.lv = (ListView)v.findViewById(R.id.match_datalist);

            if (savedInstanceState == null) {
                team = getArguments().getParcelable("team");
            }
            else {
                team = savedInstanceState.getParcelable("team");
            }

            this.autoAverage = (TextView) v.findViewById(R.id.auto_average);
            this.teleopAverage = (TextView) v.findViewById(R.id.teleop_average);
            this.specialAverage = (TextView) v.findViewById(R.id.special_average);

            new MatchPopulateTask().execute();

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
                int auto = m.getAuto();
                int teleop = m.getTeleop();
                int special = m.getSpecial();

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

        public void populateMatchList(List<Match> matches) {
            this.matches = matches;

            this.lv.setAdapter(new MatchAdapter(activity, team, matches));
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == MATCH_DETAIL && resultCode == Activity.RESULT_OK) {
                populateAverages();
            }
        }

        private class MatchPopulateTask extends AsyncTask<Void, Void, List<Match>> {
            @Override
            protected List<Match> doInBackground(Void... params) {
                List<Match> matches = ScoutingDBHelper.getInstance().getMatches(team);
                return matches;
            }

            @Override
            protected void onPostExecute(List<Match> matches) {
                populateMatchList(matches);
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
