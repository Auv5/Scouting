package com.allsaintsrobotics.scouting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created on: 9/14/13.
 */
public class SpinnerQuestion extends Question {
    String[] options;

    String selected;

    public SpinnerQuestion(int id, String question, String[] options) {
        super(id, question);
        this.options = options;
        this.selected = null;
    }

    @Override
    protected View makeEditorImpl(Context c, Team t) {
        //TODO: Implement editor
        LayoutInflater layoutInflater = LayoutInflater.from(c);

        LinearLayout layout = (LinearLayout)layoutInflater.inflate(R.layout.question_spinner, null);

        TextView spQuestion = (TextView)layout.findViewById(R.id.sp_question);

        spQuestion.setText(this.getDescription());

        Spinner sp = (Spinner)layout.findViewById(R.id.sp_spinner);

        sp.setAdapter(new ArrayAdapter<String>(c, R.id.list_item, options));

        return layout;
    }

    @Override
    public void restoreData(Team t) {

    }

    @Override
    public void setError(Team t) {

    }

    @Override
    public String getState(Team t) {
        return null;
    }

    @Override
    public void saveChanges(Team t) {

    }
}
