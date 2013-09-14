package com.allsaintsrobotics.scouting;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class NumericStatistic extends AbstractStatistic {
    String description;
    private int id;

    protected NumericStatistic(int id, String description)
    {
        this.id = id;
        this.description = description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isDerived() {
        return true;
    }

    public abstract float calculate(Team t);

    @Override
    public View makeView(Context c, Team t) {
        TextView tv = new TextView(c);

        String text;

        float calculated = this.calculate(t);

        if (calculated == ((float)((int)calculated)))
        {
            text = Integer.toString((int)calculated);
        }
        else
        {
            text = Float.toString(calculated);
        }

        tv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        tv.setText(text);

        return tv;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
