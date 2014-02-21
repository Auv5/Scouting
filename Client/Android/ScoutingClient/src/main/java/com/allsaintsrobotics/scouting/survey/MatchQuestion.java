package com.allsaintsrobotics.scouting.survey;

import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.ScoutingDBHelper;

public class MatchQuestion extends Question<Match> {
    public MatchQuestion(String label, QCustomFactory<Match> factory, int id, String[] offers) {
        super(label, factory, id, offers);
    }

    @Override
    public String dbRead(Match match) {
        return ScoutingDBHelper.getInstance().getAnswer(this, match);
    }

    @Override
    public void dbWrite(Match match, String value) {
        ScoutingDBHelper.getInstance().setAnswer(this, match, value);
    }
}
