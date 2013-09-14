package com.allsaintsrobotics.scouting;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created on: 8/4/13.
 */
public class IntegerValidator implements Validator {
    Context c;

    public IntegerValidator(Context c)
    {
        this.c = c;
    }

    @Override
    public boolean validate(String text) {
        try
        {
            // Attempt to parse the integer.
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

    private void inputSetup(View view)
    {
        ((EditText)view).setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    public void configureView(View v) {
        if (v instanceof EditText)
        {
            inputSetup(v);
        }
        else if (v instanceof ViewGroup)
        {
            ViewGroup vg = (ViewGroup)v;
            for (int i = 0; i < vg.getChildCount(); i ++)
            {
                View vt = vg.getChildAt(i);
                // Recursively go through any ViewGroups to see if we can find an EditText.
                configureView(vt);
            }
        }
    }

    @Override
    public String getError() {
        return c.getString(R.string.error_invalid_integer);
    }
}
