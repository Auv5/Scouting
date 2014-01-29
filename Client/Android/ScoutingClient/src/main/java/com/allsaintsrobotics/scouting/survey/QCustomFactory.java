package com.allsaintsrobotics.scouting.survey;

import android.content.Context;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import com.allsaintsrobotics.scouting.models.Team;
import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.survey.customs.CameraFactory;
import com.allsaintsrobotics.scouting.survey.customs.FreeResponseFactory;
import com.allsaintsrobotics.scouting.survey.customs.MultipleChoiceFactory;

/**
 * Created by jack on 11/24/13.
 */
public abstract class QCustomFactory<T> {
    private static Map<String, QCustomFactory> factories;

    static {
        factories = new HashMap<String, QCustomFactory>();

        factories.put("fr", new FreeResponseFactory<Team>());
        factories.put("mc", new MultipleChoiceFactory<Team>());
        factories.put("cam", new CameraFactory<Team>());
        factories.put("m_fr", new FreeResponseFactory<Match>());
        factories.put("m_mc", new MultipleChoiceFactory<Match>());
        factories.put("m_cam", new CameraFactory<Match>());
    }

    public abstract Form getForm(Question<T> q, T t);

    public View getValueView(Question<T> q, T t, Context c) {
        return new Viewer(t, q).getView(c);
    }

    public static QCustomFactory forId(String id) {
        return factories.get(id);
    }
}
