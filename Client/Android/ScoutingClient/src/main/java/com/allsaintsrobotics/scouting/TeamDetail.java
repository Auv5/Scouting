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
import com.allsaintsrobotics.scouting.survey.TeamQuestion;

import java.util.List;

/**
 * Created by jack on 11/24/13.
 */
public class TeamDetail extends Activity {
    private static final String TAG = "TeamDetail";
    private Team team;

    private PitFragment pf;
    private MatchFragment mf;

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


        if (!team.getConflicted()) {
            pf = PitFragment.getInstance(team);
            pitTab.setTabListener(new TeamDetail.DetailTabListener(pf));
            actionBar.addTab(pitTab);
        }

        mf = MatchFragment.getInstance(team);
        matchTab.setTabListener(new TeamDetail.DetailTabListener(mf));
        actionBar.addTab(matchTab);

        if (savedInstanceState != null && savedInstanceState.containsKey("tabstate")) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tabstate"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tabstate", getActionBar().getSelectedNavigationIndex());
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

            this.setHasOptionsMenu(true);

            return v;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.pit_scout, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_editscout:
                    openEditActivity();
                    return true;
            }
            return false;
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

        public void populateQuestionList(List<TeamQuestion> questions) {
            qa = new QuestionAdapter(this.activity, team, questions);
            lv.setAdapter(qa);
            qa.notifyDataSetChanged();
        }

        private class QuestionPopulateTask extends AsyncTask<Void, Void, List<TeamQuestion>> {
            @Override
            protected List<TeamQuestion> doInBackground(Void... params) {
                List<TeamQuestion> questions = ScoutingDBHelper.getInstance().getTeamQuestions();

                return questions;
            }

            @Override
            protected void onPostExecute(List<TeamQuestion> questions) {
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
        private ListView lv;
        private List<Match> matches;
        private Team team;
        private Activity activity;

        public static MatchFragment getInstance(Team t) {
            MatchFragment mf = new MatchFragment();

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

            new MatchPopulateTask().execute();

            return v;
        }

        public void populateMatchList(List<Match> matches) {
            this.matches = matches;

            this.lv.setAdapter(new MatchAdapter(activity, team, matches));
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
            }
            else {
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
