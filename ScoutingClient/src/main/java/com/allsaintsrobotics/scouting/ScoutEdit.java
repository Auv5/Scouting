package com.allsaintsrobotics.scouting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.allsaintsrobotics.scouting.models.Team;
import com.allsaintsrobotics.scouting.survey.Form;
import com.allsaintsrobotics.scouting.survey.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 11/25/13.
 */
public class ScoutEdit extends Activity {
    private Team team;

    private List<Form> forms;

    private static final int FORM_TOP_BOTTOM_PADDING_DP = 10;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scout_edit);

        Intent i = getIntent();

        this.team = i.getParcelableExtra("team");

        getActionBar().setTitle(String.format(getString(R.string.se_actionbar_title), team.getNumber()));

        Button saveButton = (Button) findViewById(R.id.save_stats);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveFormsExitTask().execute();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel_stats);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                // TODO: Find a better icon.
                new AlertDialog.Builder(ScoutEdit.this).setIcon(android.R.drawable.ic_dialog_alert).
                        setTitle(getString(R.string.confirm_title)).
                        setMessage("Are you sure you want to cancel? You will lose any changes you have made.").
                        setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ScoutEdit.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });

        LinearLayout formsView = (LinearLayout) findViewById(R.id.questions_cont);

        this.forms = new ArrayList<Form>();

        for (Question q : ScoutingDBHelper.getInstance().getQuestions())
        {
            Form f = q.getForm(team);
            forms.add(f);

            View answerView = f.getAnswerView(this, formsView);

            int pixelsConv = (int)(FORM_TOP_BOTTOM_PADDING_DP * getResources().getDisplayMetrics().density);

            answerView.setPadding(0, pixelsConv, 0, pixelsConv);

            formsView.addView(answerView);
        }
    }

    private class SaveFormsExitTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params) {
            for (Form f : forms) {
                if (!f.validate())
                {
                    return false;
                }
            }

            for (Form f : forms) {
                f.write();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean worked) {
            if (worked) {
                setResult(RESULT_OK);

                finish();
            }
        }
    }
}