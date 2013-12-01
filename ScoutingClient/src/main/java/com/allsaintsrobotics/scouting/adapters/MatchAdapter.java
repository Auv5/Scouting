package com.allsaintsrobotics.scouting.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.allsaintsrobotics.scouting.R;
import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.models.Team;

import java.util.List;

/**
 * Created by jack on 11/27/13.
 */
public class MatchAdapter extends ArrayAdapter<Match> {
    Team team;
    Context context;
    List<Match> matches;

    public MatchAdapter(Context context, Team team, List<Match> matches) {
        super(context, R.layout.listitem_match, matches);

        this.team = team;
        this.context = context;
        this.matches = matches;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MatchFieldHelper mfh;
        View row = convertView;

        if (row == null) {
            LayoutInflater li = LayoutInflater.from(context);

            row = li.inflate(R.layout.listitem_match, parent, false);

            mfh = new MatchFieldHelper();

            mfh.blue1 = (TextView) row.findViewById(R.id.blue_1);
            mfh.blue2 = (TextView) row.findViewById(R.id.blue_2);
            mfh.blue3 = (TextView) row.findViewById(R.id.blue_3);
            mfh.red1 = (TextView) row.findViewById(R.id.red_1);
            mfh.red2 = (TextView) row.findViewById(R.id.red_2);
            mfh.red3 = (TextView) row.findViewById(R.id.red_3);

            mfh.matchId = (TextView) row.findViewById(R.id.match_id_lv);

            row.setTag(mfh);
        }
        else {
            mfh = (MatchFieldHelper) row.getTag();
        }

        Match m = matches.get(position);

        mfh.matchId.setText(String.format(context.getString(R.string.match_id_format),
                m.getNumber()));
        mfh.blue1.setText(Integer.toString(m.getTeam(Match.Alliance.BLUE, 0).getNumber()));
        mfh.blue2.setText(Integer.toString(m.getTeam(Match.Alliance.BLUE, 1).getNumber()));
        mfh.blue3.setText(Integer.toString(m.getTeam(Match.Alliance.BLUE, 2).getNumber()));

        mfh.red1.setText(Integer.toString(m.getTeam(Match.Alliance.RED, 0).getNumber()));
        mfh.red2.setText(Integer.toString(m.getTeam(Match.Alliance.RED, 1).getNumber()));
        mfh.red3.setText(Integer.toString(m.getTeam(Match.Alliance.RED, 2).getNumber()));

        return row;
    }

    private class MatchFieldHelper {
        TextView red1, red2, red3;
        TextView blue1, blue2, blue3;

        TextView matchId;
    }
}
