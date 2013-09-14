package com.allsaintsrobotics.scouting;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jack on 7/31/13.
 */
public class MatchAdapter extends DatabaseLinkedAdapter<Match> {
    Team team;
    private int id;
    private Context context;
    List<Match> objects = null;

    public MatchAdapter(Team t, Context context, int resource) {
        super(context, resource, DatabaseManager.get().getMatchesForTeam(t));
        this.team = t;
        this.id = resource;
        this.context = context;
        this.objects = DatabaseManager.get().getMatchesForTeam(team);
    }

    public int getNumber()
    {
        return id;
    }

    @Override
    public void onChange(DatabaseManager dbm) {
        this.objects.clear();

        this.objects.addAll(DatabaseManager.get().getMatchesForTeam(team));

        super.onChange(dbm);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MatchFieldHelper matchFieldHelper;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(R.layout.listitem_match, parent, false);

            matchFieldHelper = new MatchFieldHelper();

            matchFieldHelper.red1 = (TextView) row.findViewById(R.id.red1);
            matchFieldHelper.red2 = (TextView) row.findViewById(R.id.red2);
            matchFieldHelper.red3 = (TextView) row.findViewById(R.id.red3);

            matchFieldHelper.blue1 = (TextView) row.findViewById(R.id.blue1);
            matchFieldHelper.blue2 = (TextView) row.findViewById(R.id.blue2);
            matchFieldHelper.blue3 = (TextView) row.findViewById(R.id.blue3);

            matchFieldHelper.totalRed = (TextView) row.findViewById(R.id.total_red);
            matchFieldHelper.totalBlue = (TextView) row.findViewById(R.id.total_blue);

            matchFieldHelper.matchNum = (TextView) row.findViewById(R.id.matchnum);
            matchFieldHelper.teamPoints = (TextView) row.findViewById(R.id.points_team);
            matchFieldHelper.teamFouls = (TextView) row.findViewById(R.id.fouls_team);

            row.setTag(matchFieldHelper);
        }
        else
        {
            matchFieldHelper = (MatchFieldHelper)row.getTag();
        }

        Match m = objects.get(position);

        matchFieldHelper.red1.setText(Integer.toString(m.getTeam(Match.Alliance.RED, 1).getNumber()));
        matchFieldHelper.red2.setText(Integer.toString(m.getTeam(Match.Alliance.RED, 2).getNumber()));
        matchFieldHelper.red3.setText(Integer.toString(m.getTeam(Match.Alliance.RED, 3).getNumber()));

        matchFieldHelper.blue1.setText(Integer.toString(m.getTeam(Match.Alliance.BLUE, 1).getNumber()));
        matchFieldHelper.blue2.setText(Integer.toString(m.getTeam(Match.Alliance.BLUE, 2).getNumber()));
        matchFieldHelper.blue3.setText(Integer.toString(m.getTeam(Match.Alliance.BLUE, 3).getNumber()));

        //TODO: Use obtained score or scouted total? Both have benefits.
        // Using obtained score so I don't have to fake as much data for now...
        int redTotal = m.getActualRedScore();
        int blueTotal = m.getActualBlueScore();

        if (redTotal > blueTotal)
        {
            matchFieldHelper.totalRed.setBackgroundColor(Color.GREEN);
        }
        else if (redTotal == blueTotal)
        {
            matchFieldHelper.totalBlue.setBackgroundColor(Color.GREEN);
            matchFieldHelper.totalRed.setBackgroundColor(Color.GREEN);
        }
        else
        {
            matchFieldHelper.totalBlue.setBackgroundColor(Color.GREEN);
        }

        matchFieldHelper.totalRed.setText(Integer.toString(redTotal));
        matchFieldHelper.totalBlue.setText(Integer.toString(blueTotal));

        matchFieldHelper.matchNum.setText(String.format(context.getString(R.string.match_id),
                m.getNumber()));
        matchFieldHelper.teamPoints.setText(String.format(context.getString(R.string.team_total),
                m.getTeamTotal(this.team)));
        matchFieldHelper.teamFouls.setText(String.format("Total fouls: %d",
                m.getFouls(this.team)));

        return row;
    }

    private class MatchFieldHelper
    {
        TextView red1, red2, red3, blue1, blue2, blue3;
        TextView totalRed, totalBlue;
        TextView matchNum, teamPoints, teamFouls;
    }
}
