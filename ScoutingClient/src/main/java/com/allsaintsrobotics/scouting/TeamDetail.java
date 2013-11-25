package com.allsaintsrobotics.scouting;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.allsaintsrobotics.scouting.adapters.QuestionAdapter;
import com.allsaintsrobotics.scouting.models.Team;
import com.allsaintsrobotics.scouting.survey.Question;

import java.util.List;

/**
 * Created by jack on 11/24/13.
 */
public class TeamDetail extends Activity {
    Team team;

    PitFragment pf;
    MatchFragment mf;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.team_detail);

        Intent i = getIntent();

        team = i.getParcelableExtra("team");


        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setTitle("Team " + team.getNumber());

        ActionBar.Tab pitTab = actionBar.newTab().setText("Pit");
        ActionBar.Tab matchTab = actionBar.newTab().setText("Matches");

        pf = new PitFragment();
        mf = new MatchFragment();

        pitTab.setTabListener(new TeamDetail.DetailTabListener(pf));
        matchTab.setTabListener(new TeamDetail.DetailTabListener(mf));

        actionBar.addTab(pitTab);
        actionBar.addTab(matchTab);
    }

    public class PitFragment extends Fragment {
        private ListView lv;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pitdata, container, false);

            lv = (ListView) v.findViewById(R.id.pit_datalist);

            new QuestionPopulateTask().execute();

            return v;
        }

        public void populateQuestionList(List<Question> questions) {
            lv.setAdapter(new QuestionAdapter(TeamDetail.this, questions));
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
    }

    public class MatchFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_matchdata, container, false);
        }
    }

    private class DetailTabListener implements ActionBar.TabListener {
        private Fragment fragment;

        public DetailTabListener(Fragment f) {
            this.fragment = f;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
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
