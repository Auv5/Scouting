package com.allsaintsrobotics.scouting.survey;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allsaintsrobotics.scouting.ScoutingDBHelper;
import com.allsaintsrobotics.scouting.models.Team;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jack on 11/24/13.
 */
public class Question implements Statistic {
    private FormFactory factory;
    private TextView tv;

    private int id;
    private String label;

    private static final String TAG = "Question";

    private Map<Team, String> cache;

    public Question(String label, FormFactory factory, int id) {
        this.factory = factory;
        this.id = id;
        this.label = label;

        cache = new HashMap<Team, String>();
    }

    public Form getForm(Team t) {
        return factory.getForm(this, t);
    }

    void cacheUpdate(Team t, String answer) {
        cache.put(t, answer);
    }

    public String getAnswer(Team t) {
        if (cache.containsKey(t))
        {
            return cache.get(t);
        }
        else
        {
            String value = ScoutingDBHelper.getInstance().getAnswer(this, t);
            if (value != null) {
                this.cacheUpdate(t, value);
            }

            return value;
        }
    }

    @Override
    public View getValueView(Team t, Context c) {
        String val = this.getAnswer(t);

        if (tv == null) {
            tv = new TextView(c);
        }

        tv.setText(val == null ? "" : val);

        if (tv.getParent() != null)
        {
            ((ViewGroup)tv.getParent()).removeView(tv);
        }

        return tv;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }
}
