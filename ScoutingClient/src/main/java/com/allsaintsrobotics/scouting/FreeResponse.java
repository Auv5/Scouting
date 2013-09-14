package com.allsaintsrobotics.scouting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created on: 8/3/13.
 */
public class FreeResponse extends Question {
    private int lines;
    EditText response = null;

    public FreeResponse(int id, String question) {
        this(id, question, null);
    }

    public FreeResponse(int id, String question, String[] ignored) {
        super(id, question);
        this.lines = 1;
    }

    @Override
    protected View makeEditorImpl(Context c, final Team t) {
        LayoutInflater inflater = LayoutInflater.from(c);

        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.question_freeresponse, null);
        TextView question = (TextView) layout.findViewById(R.id.fr_question);
        response = (EditText) layout.findViewById(R.id.fr_response);

        response.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                {
                    validate(t);
                }
            }
        });

        question.setText(this.getDescription());
        if (lines > 1)
        {
            response.setLines(lines);
            response.setSingleLine(false);
        }

        restoreData(t);

        return layout;
    }

    @Override
    public void restoreData(Team t)
    {
        if (this.hasAnswer(t))
        {
            response.setText(this.getAnswer(t));
        }
    }

    @Override
    public String getState(Team t) {
        if (response != null)
        {
            return response.getText().toString();
        }
        else
        {
            return "";
        }
    }

    @Override
    public void setError(Team t)
    {
        response.setError(validator.getError());
    }

    public void saveChanges(Team t)
    {
        this.setAnswerForTeam(t, response.getText());
    }
}
