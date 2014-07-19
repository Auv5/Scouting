package com.allsaintsrobotics.scouting.survey;

import android.content.Context;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import com.allsaintsrobotics.scouting.models.Team;
import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.survey.customs.AutoAAFactory;
import com.allsaintsrobotics.scouting.survey.customs.CameraFactory;
import com.allsaintsrobotics.scouting.survey.customs.FreeResponseFactory;
import com.allsaintsrobotics.scouting.survey.customs.MultipleChoiceFactory;
import com.allsaintsrobotics.scouting.survey.customs.TeleopAAFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jack on 11/24/13.
 * This file is a part of the ASTECHZ Scouting app.
 */
public abstract class QCustomFactory<T> {
    private static final Map<String, QCustomFactory> factories;

    static {
        factories = new HashMap<String, QCustomFactory>();

        factories.put("fr", new FreeResponseFactory<Team>());
        factories.put("mc", new MultipleChoiceFactory<Team>());
        factories.put("cam", new CameraFactory<Team>());
        factories.put("m_fr", new FreeResponseFactory<Match>());
        factories.put("m_mc", new MultipleChoiceFactory<Match>());
        factories.put("m_cam", new CameraFactory<Match>());
        factories.put("m_aaauto", new AutoAAFactory<Match>());
        factories.put("m_aateleop", new TeleopAAFactory<Match>());
    }

    public abstract Form<T> getForm(Question<T> q, T t);

    public JSONObject getJSON(Question<T> q, T t) {
        JSONObject json = new JSONObject();

        try {
            json.put("value", q.getAnswer(t));
            return json;
        } catch (JSONException ignored) { }

        return null;
    }

    public View getValueView(Question<T> q, T t, Context c) {
        return new Viewer(t, q).getView(c);
    }

    public static QCustomFactory forId(String id) {
        return factories.get(id);
    }
}
