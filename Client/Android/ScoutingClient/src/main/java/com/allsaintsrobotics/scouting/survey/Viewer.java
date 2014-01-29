package com.allsaintsrobotics.scouting.survey;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Jack on 28/01/14.
 */
public class Viewer<T> {
    private T t;
    private Question<T> question;

    private TextView tv;

    public Viewer(T t, Question<T> q) {
        this.t = t;
        this.question = q;
    }

    public View getView(Context c) {
        String val = question.getAnswer(t);

        if (tv == null) {
            tv = new TextView(c);
        }

        tv.setText(val == null ? question.getDefaultPrompt() : val);

        if (tv.getParent() != null) {
            ((ViewGroup)tv.getParent()).removeView(tv);
        }

        return tv;
    }
}
