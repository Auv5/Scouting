package com.allsaintsrobotics.scouting;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jack on 7/31/13.
 */
public class TeamAdapter extends DatabaseLinkedAdapter<Team> {
    private int id;
    private Context context;
    private List<Team> objects = null;

    public TeamAdapter(Context context, int resource, List<Team> objects) {
        super(context, resource, objects);

        this.id = resource;
        this.context = context;
        this.objects = objects;
        DatabaseManager.get().addTeamChangedListener(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        TeamHolder holder;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(this.id, parent, false);

            holder = new TeamHolder();
            holder.team = (TextView)row.findViewById(R.id.team);

            holder.teamName = (TextView)row.findViewById(R.id.team_name);

            row.setTag(holder);
        }

        else
        {
            holder = (TeamHolder)row.getTag();
        }

        Team t = objects.get(position);

        holder.teamName.setText(t.getName().toUpperCase());

        holder.team.setText("Team " + t.getNumber());

        return row;
    }

    static class TeamHolder
    {
        TextView team;
        TextView teamName;
    }
}
