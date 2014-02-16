package com.allsaintsrobotics.scouting.survey.customs;

import android.content.Context;
import android.view.View;

import com.allsaintsrobotics.scouting.survey.Form;
import com.allsaintsrobotics.scouting.survey.QCustomFactory;
import com.allsaintsrobotics.scouting.survey.Question;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jack on 29/01/14.
 */
public class AutoAAFactory<T> extends QCustomFactory<T> {
    @Override
    public Form getForm(Question<T> q, T t) {
        return new AutoAAForm<T>(q, t);
    }

    @Override
    public View getValueView(Question<T> q, T t, Context c) {
        return new AutoAAViewer<T>(t, q).getView(c);
    }

    @Override
    public JSONObject getJSON(Question<T> q, T t) {
        try {
            String answer = q.getAnswer(t);
            if (answer == null) return new JSONObject();
            else return new JSONObject(answer);
        } catch (JSONException ignored) { }

        return null;
    }
}
