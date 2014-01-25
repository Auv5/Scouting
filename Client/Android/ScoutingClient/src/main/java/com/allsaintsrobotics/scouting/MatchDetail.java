package com.allsaintsrobotics.scouting;

import com.allsaintsrobotics.scouting.ScoutingDBHelper;
import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.adapters.QuestionAdapter;
import com.allsaintsrobotics.scouting.survey.MatchQuestion;
import com.allsaintsrobotics.scouting.R;

import android.widget.AdapterView;
import android.widget.ListView;
import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.List;

public class MatchDetail extends Activity {
    public static final int REQUEST_EDIT_MATCH = 54;

    ListView mainLv;
    QuestionAdapter<Match> adapter;
    Match match;
    List<MatchQuestion> questions;

    public void onCreate(Bundle state) {
        super.onCreate(state);

        this.setContentView(R.layout.match_detail);

        this.mainLv = (ListView)findViewById(R.id.match_questionlist);

        mainLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.setClass(MatchDetail.this, MatchEdit.class);
                
                int[] questionIdArr = new int[questions.size()];
                
                for (int j = 0; j < questions.size(); j ++) {
                    questionIdArr[j] = questions.get(j).getId();
                }

                i.putExtra("questions", questionIdArr);
                i.putExtra("current", position);
                i.putExtra("match", match);

                startActivityForResult(i, REQUEST_EDIT_MATCH);
            }
        });


        this.match = getIntent().getParcelableExtra("match");

        getActionBar().setTitle(String.format(getString(R.string.md_actionbar_title), this.match.getNumber()));

        new LoadMatchQuestionsTask().execute();
    }

    

    private void populateQuestionList(List<MatchQuestion> result) {
        this.questions = result;
        adapter = new QuestionAdapter<Match>(this, this.match, result);
        this.mainLv.setAdapter(adapter);
    }

    private class LoadMatchQuestionsTask extends AsyncTask<Void, Void, List<MatchQuestion>> {
        protected List<MatchQuestion> doInBackground(Void... params) {
            return ScoutingDBHelper.getInstance().getMatchQuestions();
        }

        protected void onPostExecute(List<MatchQuestion> result) {
            populateQuestionList(result);
        }
    }
}