package com.allsaintsrobotics.scouting.survey;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.allsaintsrobotics.scouting.models.Team;
import com.allsaintsrobotics.scouting.models.Match;

/**
 * Created by jack on 11/24/13.
 */
public abstract class FormFactory<T> {
    private static Map<String, FormFactory> factories;

    static {
        factories = new HashMap<String, FormFactory>();

        factories.put("fr", new FreeResponseForm.FreeResponseFormFactory<Team>());
        factories.put("mc", new MultipleChoiceForm.MultipleChoiceFormFactory<Team>());
        factories.put("cam", new CameraForm.CameraFormFactory<Team>());
        factories.put("m_fr", new FreeResponseForm.FreeResponseFormFactory<Match>());
        factories.put("m_mc", new MultipleChoiceForm.MultipleChoiceFormFactory<Match>());
        factories.put("m_cam", new CameraForm.CameraFormFactory<Match>());
    }

    public abstract Form getForm(Question<T> q, T t);

    public static FormFactory forId(String id) {
        return factories.get(id);
    }
}
