package com.allsaintsrobotics.scouting.survey;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.allsaintsrobotics.scouting.models.Team;

/**
 * Created by jack on 11/24/13.
 */
public abstract class AbstractStatistic implements Statistic {
    private String label;
    private TextView tv;

    protected AbstractStatistic(String label) {
        this.label = label;
    }

    @Override
    public View getValueView(Team t, Context c) {
        String val = this.getValue(t);

        if (tv == null) {
            tv = new TextView(c);
        }

        tv.setText(val);

        return tv;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public abstract String getValue(Team t);
}
