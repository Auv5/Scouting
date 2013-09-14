package com.allsaintsrobotics.scouting;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ScoutEdit extends Activity {
    Team t;

    List<Statistic> stats;

    /**
     * Padding on all sides of the
     */
    public static final int QUESTION_PADDING = 20;
    private LinearLayout questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout);

        t = getIntent().getParcelableExtra("team");

        TextView scoutingTeam = (TextView) findViewById(R.id.scouting_team);

        scoutingTeam.setText(String.format(getString(R.string.scouting_team), t.getNumber(), t.getName()));

        questions = (LinearLayout) findViewById(R.id.questions);

        stats = DatabaseManager.get().getStats();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus)
        {
            float scale = getResources().getDisplayMetrics().density;
            int paddingPx = Math.round(QUESTION_PADDING * scale);
            boolean regening = questions.getChildCount() == 0;
                for (Statistic s : stats)
                {
                    if (s instanceof Question)
                    {
                        Question q = (Question)s;

                        if (regening)
                        {
                            View v = q.makeEditor(this, t);
                            v.setPadding(0, paddingPx, 0, paddingPx);
                            questions.addView(v);
                        }
                        else
                        {
                            q.restoreData(t);
                        }
                    }
                }
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onBackPressed() {
        boolean goBack = true;

        for (Statistic s : stats)
        {
            if (!s.isDerived())
            {
                Question q = (Question)s;

                if (!q.validate(t))
                {
                    goBack = false;
                }
                // Write the answers to database.
            }
        }

        if (goBack)
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        saveChanges();
        super.onPause();
    }

    public void saveChanges()
    {
        for (Statistic s : stats)
        {
            if (!s.isDerived())
            {
                ((Question)s).saveChanges(t);
                ((Question)s).writeTo(t, DatabaseManager.get());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scout_edit, menu);
        return true;
    }
}
