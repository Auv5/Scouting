package com.allsaintsrobotics.scouting;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on: 8/13/13.
 */
public class MockSyncManager implements SyncManager {
    @Override
    public void run()
    {

    }

    @Override
    public List<Operation<Statistic>> syncStatistics(SQLiteDatabase db) {
        return new ArrayList<Operation<Statistic>>();
    }

    @Override
    public List<Operation<Team>> syncTeams(SQLiteDatabase db) {
        return new ArrayList<Operation<Team>>();
    }

    @Override
    public List<Operation<Match>> syncMatchesScores(List<Team> teams, SQLiteDatabase db) {
        return new ArrayList<Operation<Match>>();
    }
}
