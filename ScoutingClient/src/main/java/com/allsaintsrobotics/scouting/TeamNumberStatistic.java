package com.allsaintsrobotics.scouting;

/**
 * Created by Jack on 8/1/13.
 */
public class TeamNumberStatistic extends NumericStatistic {
    public TeamNumberStatistic(int id) {
        super(id, "Team Number");
    }

    @Override
    public boolean isDerived() {
        return true;
    }

    @Override
    public float calculate(Team t) {
        return t.getNumber();
    }
}
