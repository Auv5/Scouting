package com.allsaintsrobotics.scouting.survey;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.allsaintsrobotics.scouting.R;
import com.allsaintsrobotics.scouting.models.Team;

/**
 * Created by jack on 11/24/13.
 */
public class FreeResponseForm extends Form {
    private View view;

    private TextView label;
    private EditText response;

    public FreeResponseForm(Question q, Team t) {
        super(q, t);
    }

    @Override
    public View getAnswerView(Context c, ViewGroup parent) {
        if (view == null)
        {
            LayoutInflater li = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.view = li.inflate(R.layout.question_freeresponse, null);
        }

        this.label = (TextView) view.findViewById(R.id.fr_label);

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

        String answer = question.getAnswer(team);

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

    public static class FreeResponseFormFactory extends FormFactory {
        // Ensure this is the only constructor so FormFactory doesn't mess up.
        public FreeResponseFormFactory() {}

        @Override
        public Form getForm(Question q, Team t) {
            return new FreeResponseForm(q, t);
        }
    }
}
