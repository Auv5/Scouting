package com.allsaintsrobotics.scouting;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.Button;

import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.survey.Form;

public class MatchEdit extends Activity {
    LinearLayout root;
    Button prev, next;
    
    Form form;
    Match match;
    int[] questions;
    int currentIndex;

    private void setCurrentQuestion(int index) {
        currentIndex = index;
        if (form != null) {
            form.write();
        }

        form = ScoutingDBHelper.getInstance().getMatchQuestion(questions[index]).getForm(match);
        root.removeAllViews();
        root.addView(form.getAnswerView(this, root));
    }

    public void onCreate(Bundle state) {
        super.onCreate(state);

        Bundle extras = getIntent().getExtras();

        this.setContentView(R.layout.match_edit);

        root = (LinearLayout) findViewById(R.id.matchedit_root);
        
        match = extras.getParcelable("match");

        questions = extras.getIntArray("questions");

        currentIndex = extras.getInt("current", -1);
        
        setCurrentQuestion(currentIndex);    

        prev = (Button) findViewById(R.id.matchedit_prev);
        next = (Button) findViewById(R.id.matchedit_next);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex > 0) {
                    setCurrentQuestion(currentIndex-1);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex != questions.length - 1) {
                    setCurrentQuestion(currentIndex+1);
                }
            }
        });

        getActionBar().setTitle(String.format(getString(R.string.match_edit_title), match.getNumber(),
                match.getScout()));
    }

    @Override
    public void onBackPressed() {
        if (form != null) {
            form.write();
        }

        this.setResult(RESULT_OK);
        finish();
    }
}
