package com.allsaintsrobotics.scouting.survey.customs;

import android.content.Context;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.allsaintsrobotics.scouting.R;
import com.allsaintsrobotics.scouting.survey.Form;
import com.allsaintsrobotics.scouting.survey.Question;

/**
 * Created by jack on 11/24/13.
 * This file is a part of the ASTECHZ Scouting app.
 */
public class FreeResponseForm<T> extends Form<T> {
    private View view;

    private EditText response;

    public FreeResponseForm(Question q, T t) {
        super(q, t);
    }

    @Override
    public View getAnswerView(Activity c, ViewGroup parent) {
        if (view == null)
        {
            LayoutInflater li = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.view = li.inflate(R.layout.question_freeresponse, null);
        }

        TextView label = (TextView) view.findViewById(R.id.fr_label);

        this.response = (EditText) view.findViewById(R.id.fr_response);

        label.setText(question.getLabel());

        response.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                FreeResponseForm.this.validate();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        String answer = question.getAnswer(t);

        if (answer != null)
        {
            response.setText(answer);
        }

        return view;
    }

    @Override
    public String getAnswer() {
        return response.getText().toString();
    }

    @Override
    public void setError(String error) {
        response.setError(error);
    }
}
