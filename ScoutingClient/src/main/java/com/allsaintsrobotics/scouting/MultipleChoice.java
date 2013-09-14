package com.allsaintsrobotics.scouting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by Jack on 8/1/13.
 */
public class MultipleChoice extends Question {
    String[] options;
    RadioButton selected;
    RadioGroup group;

    public MultipleChoice(int id, String question, String[] options) {
        super(id, question);

        this.options = options;
    }

    @Override
    protected View makeEditorImpl(Context c, final Team t) {
        //TODO: Implement editor
        LayoutInflater layoutInflater = LayoutInflater.from(c);

        LinearLayout layout = (LinearLayout)layoutInflater.inflate(R.layout.question_multiplechoice, null);

        TextView mcQuestion = (TextView) layout.findViewById(R.id.mc_question);

        mcQuestion.setText(this.getDescription());

        group = (RadioGroup) layout.findViewById(R.id.options);

        for (int i = 0; i < options.length; i ++)
        {
            RadioButton button = (RadioButton) layoutInflater.inflate(R.layout.option_multiplechoice,
                    null);

            button.setId(i+1);

            button.setText(options[i]);

            group.addView(button);
        }

        assert group != null;
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton changedRadioButton = (RadioButton)group.findViewById(checkedId);

                if (changedRadioButton.isChecked())
                {
                    selected = changedRadioButton;
                }
            }
        });

        restoreData(t);

        return layout;

//        TextView tv = new TextView(c);
//
//        tv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
//
//        tv.setText("Hello, again.");
//
//        return tv;
    }

    @Override
    public void restoreData(Team t)
    {
        String answer = (String)this.getAnswer(t);

        for (int i = 0; i < options.length; i ++)
        {
            RadioButton button = (RadioButton) group.getChildAt(i);
            if (answer != null && options[i].equals(this.getAnswer(t)))
            {
                group.check(button.getId());
                selected = button;
            }
        }
    }

    @Override
    public void setError(Team t)
    {
        return;
    }

    @Override
    public String getState(Team t) {
        return selected.getText().toString();
    }

    @Override
    public void saveChanges(Team t)
    {
        if (selected != null)
        {
            this.setAnswerForTeam(t, selected.getText());
        }
    }
}
