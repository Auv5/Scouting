package com.allsaintsrobotics.scouting.survey;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;

import com.allsaintsrobotics.scouting.ScoutingDBHelper;

/**
 * Created by jack on 11/24/13.
 */
public abstract class Form<T> {
    protected Question<T> question;
    protected T t;
    private Validator validator;

    public Form(Question<T> q, T t) {
        this.question = q;
        this.t = t;
    }

    public abstract View getAnswerView(Activity c, ViewGroup parent);

    public abstract String getAnswer();

    public abstract void setError(String error);

    public void write() {
        String answer = this.getAnswer();
        if (answer != null && !answer.isEmpty()) {
           question.write(t, answer);
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

    public boolean result(Activity c, int request, int response, Intent data) {
        return false;
    }

    public interface Validator {
        public boolean validate(String text);
        public String getError();
    }
}
