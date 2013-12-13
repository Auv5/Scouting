package com.allsaintsrobotics.scouting.survey;

import android.content.Context;
import android.view.View;

import com.allsaintsrobotics.scouting.models.Team;

/**
 * Created by jack on 11/24/13.
 */
public interface Statistic {
    public View getValueView(Team t, Context c);

    public String getLabel();
}
