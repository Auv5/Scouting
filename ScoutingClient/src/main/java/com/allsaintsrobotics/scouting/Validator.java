package com.allsaintsrobotics.scouting;

import android.view.View;

/**
 * Created on: 8/4/13.
 */
public interface Validator
{
    public boolean validate(String text);
    public void configureView(View v);
    public String getError();
}
