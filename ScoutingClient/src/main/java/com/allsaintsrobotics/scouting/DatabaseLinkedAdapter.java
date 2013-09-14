package com.allsaintsrobotics.scouting;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created on: 9/11/13.
 */
public abstract class DatabaseLinkedAdapter<T> extends ArrayAdapter<T> implements DatabaseManager.ChangeListener {
    public DatabaseLinkedAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }

    @Override
    public void onChange(DatabaseManager dbm) {
        this.notifyDataSetChanged();
    }
}
