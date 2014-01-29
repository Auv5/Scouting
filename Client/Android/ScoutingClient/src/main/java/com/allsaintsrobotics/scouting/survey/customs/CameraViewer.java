package com.allsaintsrobotics.scouting.survey.customs;

import android.content.Context;
import android.view.View;

import com.allsaintsrobotics.scouting.survey.Question;
import com.allsaintsrobotics.scouting.survey.Viewer;

/**
 * Created by Jack on 28/01/14.
 */
public class CameraViewer<T> extends Viewer<T> {
    public CameraViewer(T t, Question<T> q) {
        super(t, q);
    }

    @Override
    public View getView(Context c) {
        //TODO: Show image
        return super.getView(c);
    }
}
