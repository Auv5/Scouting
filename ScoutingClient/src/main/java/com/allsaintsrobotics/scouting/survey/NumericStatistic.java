package com.allsaintsrobotics.scouting.survey;

import com.allsaintsrobotics.scouting.models.Team;

/**
 * Created by jack on 11/24/13.
 */
public abstract class NumericStatistic extends AbstractStatistic {
    protected NumericStatistic(String label) {
        super(label);
    }

    @Override
    public String getValue(Team t) {
        return Float.toString(getNumericValue(t));
    }

    public abstract float getNumericValue(Team t);
}
