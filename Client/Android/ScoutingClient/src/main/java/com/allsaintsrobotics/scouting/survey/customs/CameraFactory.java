package com.allsaintsrobotics.scouting.survey.customs;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.allsaintsrobotics.scouting.survey.Form;
import com.allsaintsrobotics.scouting.survey.QCustomFactory;
import com.allsaintsrobotics.scouting.survey.Question;

import java.io.File;

/**
* Created by Jack on 28/01/14.
*/
public class CameraFactory<T> extends QCustomFactory<T> {
    private static final String TAG = "CameraFormFactory";

    public CameraFactory() {}

    @Override
    public Form getForm(Question<T> q, T t) {
        String dir = "scouting_pics";

        File sdcard = Environment.getExternalStorageDirectory();

        //TODO: Handle no SD card case

        File dirFile = new File(sdcard, dir);

        // Ensure directory exists.
        dirFile.mkdir();

        String filename = String.valueOf(q.getId()) + "_" + t.hashCode() + ".jpg";

        Log.d(TAG, "Camera filename: " + filename);

        File newFile = new File(dirFile, filename);

        return new CameraForm<T>(q, t, newFile);
    }

    @Override
    public View getValueView(Question<T> q, T m, Context c) {
        return new CameraViewer<T>(m, q).getView(c);
    }
}
