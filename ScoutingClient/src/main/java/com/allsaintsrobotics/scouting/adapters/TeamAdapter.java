package com.allsaintsrobotics.scouting.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.allsaintsrobotics.scouting.R;
import com.allsaintsrobotics.scouting.ScoutingDBHelper;
import com.allsaintsrobotics.scouting.models.Team;

/**
* Created by jack on 11/24/13.
*/
public class TeamAdapter extends CursorAdapter {
    public TeamAdapter(Context context, Cursor c) {
        super(context, c, false);
    }

    /* Creates a blank view */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(context);

        View v = li.inflate(R.layout.listitem_team, parent, false);

        return v;
    }

    /* Binds data from the Cursor to the View */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvNumber = (TextView) view.findViewById(R.id.tv_teamnumber);
        TextView tvName = (TextView) view.findViewById(R.id.tv_teamname);

        int number = cursor.getInt(cursor.getColumnIndex(ScoutingDBHelper.TEAM_NUM));
        String name = cursor.getString(cursor.getColumnIndex(ScoutingDBHelper.TEAM_NAME));

        Team t = new Team(number, name);

        view.setTag(t);

        tvNumber.setText(String.format(context.getString(R.string.team_number), number));
        tvName.setText(String.format(context.getString(R.string.team_name), name));
    }
}
