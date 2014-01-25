package com.allsaintsrobotics.scouting.survey;

import com.allsaintsrobotics.scouting.models.Team;
import com.allsaintsrobotics.scouting.ScoutingDBHelper;

public class TeamQuestion extends Question<Team> {
    public TeamQuestion(String label, FormFactory<Team> factory, int id, String[] offers) {
        super(label, factory, id, offers);
    }

    @Override
    public String dbRead(Team team) {
        return ScoutingDBHelper.getInstance().getAnswer(this, team);
    }

    @Override
    public void dbWrite(Team team, String value) {
        ScoutingDBHelper.getInstance().setAnswer(this, team, value);
    }
    
    @Override
    protected String getDefaultPrompt() {
        return "";
    }
}
