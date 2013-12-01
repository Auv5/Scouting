package com.allsaintsrobotics.scouting.survey;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.allsaintsrobotics.scouting.ScoutEdit;
import com.allsaintsrobotics.scouting.ScoutingDBHelper;
import com.allsaintsrobotics.scouting.models.Team;

/**
 * Created by jack on 11/24/13.
 */
public abstract class Form {
    protected Question question;
    protected Team team;
    private Validator validator;

    public Form(Question q, Team t) {
        this.question = q;
        this.team = t;
    }

    public abstract View getAnswerView(ScoutEdit c, ViewGroup parent);

    public abstract String getAnswer();

    public abstract void setError(String error);

    public void write() {
        String answer = this.getAnswer();
        if (answer != null && !answer.isEmpty()) {
            ScoutingDBHelper.getInstance().setAnswer(question, team, answer);
            // Update the question cache (also will allow for the list to work)
            question.cacheUpdate(team, answer);
        }
    }

    public void setValidator(Validator v) {
        this.validator = v;
    }

    public boolean validate() {
        if (validator == null) {
            return true;
        }
        else if (validator.validate(getAnswer())) {
            return true;
        }
        else {
            this.setError(validator.getError());
            return false;
        }
    }

    public boolean result(ScoutEdit c, int request, int response, Intent data) {
        return false;
    }

    public interface Validator {
        public boolean validate(String text);
        public String getError();
    }
}
