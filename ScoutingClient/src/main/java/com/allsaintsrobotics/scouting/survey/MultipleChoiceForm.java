package com.allsaintsrobotics.scouting.survey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.allsaintsrobotics.scouting.R;
import com.allsaintsrobotics.scouting.models.Team;

/**
 * Created by jack on 11/25/13.
 */
public class MultipleChoiceForm extends Form {
    String[] offers;
    View view;

    private RadioGroup group;
    private TextView label;

    private RadioButton selected;

    public MultipleChoiceForm(Question question, Team team, String[] offers) {
        super(question, team);

        this.offers = offers;
    }

    @Override
    public View getAnswerView(Context c, ViewGroup parent) {
        if (view == null) {
            // Basic layout inflate pattern.
            LayoutInflater li = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout) li.inflate(R.layout.question_multiplechoice, null);

            this.group = (RadioGroup) layout.findViewById(R.id.mc_group);

            for (int i = 0; i < offers.length; i ++)
            {
                RadioButton rb = (RadioButton) li.inflate(R.layout.option_multiplechoice, null);

                rb.setId(i+1);
                rb.setText(offers[i]);

                group.addView(rb);
            }

            this.view = layout;
        }

        this.label = (TextView) view.findViewById(R.id.mc_label);

        label.setText(question.getLabel());

        this.selected = null;

        this.group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton changedButton = (RadioButton)group.findViewById(checkedId);

                // "Changed" events occur for both checking and unchecking.
                if (changedButton.isChecked()) {
                    MultipleChoiceForm.this.selected = changedButton;
                }
            }
        });

        String answer = question.getAnswer(team);

        if (answer != null) {
            for (int i = 0; i < group.getChildCount(); i ++) {
                RadioButton rb = (RadioButton)group.getChildAt(i);

                if (rb.getText().equals(answer)) {
                    group.check(rb.getId());
                    rb.setChecked(true);
                }
            }
        }

        return view;
    }

    @Override
    public String getAnswer() {
        if (selected != null) {
            return selected.getText().toString();
        }
        else {
            return null;
        }
    }

    @Override
    public void setError(String error) {
        label.setError(error);
    }

    public static class MultipleChoiceFormFactory extends FormFactory {
        public MultipleChoiceFormFactory() {}

        @Override
        public Form getForm(Question q, Team t) {
            return new MultipleChoiceForm(q, t, offers);
        }
    }
}
