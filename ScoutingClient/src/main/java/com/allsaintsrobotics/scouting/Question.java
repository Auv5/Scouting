package com.allsaintsrobotics.scouting;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created on: 7/31/13.
 */
public abstract class Question extends AbstractStatistic {
    private String desc;
    private int id;

    protected Map<Team, CharSequence> data;
    protected Validator validator = null;

    protected Question(int id, String question)
    {
        this.id = id;
        desc = question;
        data = new HashMap<Team, CharSequence>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isDerived() {
        return false;
    }

    public final View makeEditor(Context c, Team t)
    {
        View v = makeEditorImpl(c, t);

        if (validator != null)
        {
            validator.configureView(v);
        }

        return v;
    }

    protected abstract View makeEditorImpl(Context c, Team t);

    public abstract void restoreData(Team t);

    @Override
    public View makeView(Context c, Team t) {
        TextView tv = new TextView(c);
        tv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        tv.setText(this.data.get(t));

        return tv;
    }

    public CharSequence getAnswer(Team t)
    {
        return data.get(t);
    }

    public String getDescription()
    {
        return desc;
    }

    public abstract void setError(Team t);

    public abstract String getState(Team t);

    public boolean validate(Team t)
    {
        if (validator == null || TextUtils.isEmpty(getState(t)))
        {
            return true;
        }

        boolean v = validator.validate(getState(t));
        if (!v)
        {
            setError(t);
        }

        return v;
    }

    protected void setValidator(Validator v)
    {
        this.validator = v;
    }

    protected void setAnswerForTeam(Team t, CharSequence text)
    {
        this.data.put(t, text);
    }

    public abstract void saveChanges(Team t);

    public boolean hasAnswer(Team t) {
        return data.containsKey(t);
    }

    public void addAnswer(Team team, String string) {
        this.data.put(team, string);
    }

    public void writeTo(Team t, DatabaseManager databaseManager)
    {
        CharSequence answer = this.data.get(t);
        if (answer != null)
        {
            databaseManager.recordAnswer(this, t, answer);
        }
    }
}
