/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.allsaintsrobotics.scouting.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.allsaintsrobotics.scouting.R;
import com.allsaintsrobotics.scouting.MatchDetail;
import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.models.Team;

import java.util.List;

/**
 * Created by jack on 11/27/13.
 * This file is a part of the ASTECHZ Scouting app.
 */
public class MatchAdapter extends ArrayAdapter<Match> {
    private final Team team;
    private final Context context;
    private final List<Match> matches;

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

        final Match m = matches.get(position);

        mfh.matchId.setText(String.format(context.getString(R.string.match_id_format),
                m.getNumber()));
        mfh.blue1.setText(Integer.toString(m.getTeam(Match.Alliance.BLUE, 0)));
        mfh.blue2.setText(Integer.toString(m.getTeam(Match.Alliance.BLUE, 1)));
        mfh.blue3.setText(Integer.toString(m.getTeam(Match.Alliance.BLUE, 2)));

        mfh.red1.setText(Integer.toString(m.getTeam(Match.Alliance.RED, 0)));
        mfh.red2.setText(Integer.toString(m.getTeam(Match.Alliance.RED, 1)));
        mfh.red3.setText(Integer.toString(m.getTeam(Match.Alliance.RED, 2)));

        if (mfh.blue1.getText().toString().equals(Integer.toString(m.getScout()))) {
            mfh.blue1.setTypeface(null, Typeface.BOLD);
        }
        else {
            mfh.blue1.setTypeface(null, Typeface.NORMAL);
        }

        if (mfh.blue2.getText().toString().equals(Integer.toString(m.getScout()))) {
            mfh.blue2.setTypeface(null, Typeface.BOLD);
        }
        else {
            mfh.blue2.setTypeface(null, Typeface.NORMAL);
        }

        if (mfh.blue3.getText().toString().equals(Integer.toString(m.getScout()))) {
            mfh.blue3.setTypeface(null, Typeface.BOLD);
        }
        else {
            mfh.blue3.setTypeface(null, Typeface.NORMAL);
        }

        if (mfh.red1.getText().toString().equals(Integer.toString(m.getScout()))) {
            mfh.red1.setTypeface(null, Typeface.BOLD);
        }
        else {
            mfh.red1.setTypeface(null, Typeface.NORMAL);
        }

        if (mfh.red2.getText().toString().equals(Integer.toString(m.getScout()))) {
            mfh.red2.setTypeface(null, Typeface.BOLD);
        }
        else {
            mfh.red2.setTypeface(null, Typeface.NORMAL);
        }

        if (mfh.red3.getText().toString().equals(Integer.toString(m.getScout()))) {
            mfh.red3.setTypeface(null, Typeface.BOLD);
        }
        else {
            mfh.red3.setTypeface(null, Typeface.NORMAL);
        }
        
        if (isEnabled(position)) {
            row.setClickable(true);
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent();
                    
                    i.setClass(context, MatchDetail.class);
                    
                    i.putExtra("match", m);

                    context.startActivity(i);
                }
            });
        }
        else {
            row.setClickable(false);
        }

        return row;
    }

    @Override
    public boolean isEnabled(int position) {
        // If this isn't the team we're scouting for this match, disable it.
        return !(team != null && matches.get(position).getScout() != team.getNumber()) && super.isEnabled(position);
    }

    private class MatchFieldHelper {
        TextView red1, red2, red3;
        TextView blue1, blue2, blue3;

        TextView matchId;
    }
}
