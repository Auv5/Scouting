package com.allsaintsrobotics.scouting;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created on: 8/13/13.
 */
public interface SyncManager extends Runnable {
    public List<Operation<Statistic>> syncStatistics(SQLiteDatabase db);

    public List<Operation<Team>> syncTeams(SQLiteDatabase db);

    public List<Operation<Match>> syncMatchesScores(List<Team> teams, SQLiteDatabase db);

    public class OfferedAnswer
    {
        public int question;
        public int ansNum;
        public String offer;
    }

    public class Operation<T>
    {
        public static final int ADDITION = 0;
        public static final int DELETION = 1;

        private T operateOn;
        private int operation;

        public Operation(T operateOn, int operation)
        {
            this.operateOn = operateOn;
            this.operation = operation;
        }

        public int getOperation()
        {
            return operation;
        }

        public T getOperateOn()
        {
            return operateOn;
        }


    }
}
