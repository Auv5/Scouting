package com.allsaintsrobotics.scouting.survey.customs;

import com.allsaintsrobotics.scouting.survey.Question;
import com.allsaintsrobotics.scouting.survey.Viewer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jack on 30/01/14.
 */
public class AutoAAViewer<T> extends Viewer<T> {
    public AutoAAViewer(T t, Question<T> q) {
        super(t, q);
    }

    @Override
    protected String textFilter(String text) {
        if (text == null) {
            return "";
        }

        try {
            JSONObject json = new JSONObject(text);

            return Integer.toString(json.getInt("score"));
        } catch (JSONException e) {
            return "";
        }
    }
}
