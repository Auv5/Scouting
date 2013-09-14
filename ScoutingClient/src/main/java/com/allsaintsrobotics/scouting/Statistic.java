package com.allsaintsrobotics.scouting;

import android.content.Context;
import android.view.View;

/**
 * Created by Jack on 8/1/13.
 */
public interface Statistic
{
    /**
     * If true, this statistic is not editable, and will ignore the "editMode" parameter.
     */
    public boolean isDerived();

    public View makeView(Context c, Team team);

    public String getDescription();

    String getTypeString();

    int getId();
}
