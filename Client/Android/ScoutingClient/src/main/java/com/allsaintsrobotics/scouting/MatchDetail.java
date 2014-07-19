package com.allsaintsrobotics.scouting;

import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.adapters.QuestionAdapter;
import com.allsaintsrobotics.scouting.survey.MatchQuestion;

import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.app.Activity;
import android.view.MenuItem;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.List;

public class MatchDetail extends Activity {
    private static final int REQUEST_EDIT_MATCH = 54;

    private ListView mainLv;
    private QuestionAdapter<Match> adapter;

    private Match match;
    private List<MatchQuestion> questions;

    private void openMatchQuestion(int index) {
        Intent i = new Intent();
        i.setClass(MatchDetail.this, MatchEdit.class);

        int[] questionIdArr = new int[questions.size()];

        for (int j = 0; j < questions.size(); j ++) {
            questionIdArr[j] = questions.get(j).getId();
        }

        i.putExtra("questions", questionIdArr);
        i.putExtra("current", index);
        i.putExtra("match", match);

        startActivityForResult(i, REQUEST_EDIT_MATCH);
    }

    public void onCreate(Bundle state) {
        super.onCreate(state);

        this.setContentView(R.layout.match_detail);

        this.mainLv = (ListView)findViewById(R.id.match_questionlist);

        this.match = getIntent().getParcelableExtra("match");

        getActionBar().setTitle(String.format(getString(R.string.md_actionbar_title),
                this.match.getNumber(), this.match.getScout()));

        new LoadMatchQuestionsTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = this.getMenuInflater();
        mi.inflate(R.menu.match_detail, menu);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_editmatch:
                openMatchQuestion(0);
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_MATCH && resultCode == RESULT_OK && adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void populateQuestionList(List<MatchQuestion> result) {
        this.questions = result;
        this.adapter = new QuestionAdapter<Match>(this, this.match, result);
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
