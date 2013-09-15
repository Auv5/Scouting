package com.allsaintsrobotics.scouting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on: 8/12/13.
 */
public class DatabaseManager extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "teamdb";

    public static final int VERSION = 1;

    private List<Match> matches;
    private List<Team> teams;
    private List<Statistic> stats;

    private List<ChangeListener> teamListeners;

    private SQLiteDatabase mDb = null;
    private SyncManager sm = null;

    public static DatabaseManager instance = null;

    private DatabaseManager(Context c, SyncManager sm)
    {
        super(c, DATABASE_NAME, null, VERSION);
        this.sm = sm;
        mDb = this.getWritableDatabase();

        matches = new ArrayList<Match>();
        teams = new ArrayList<Team>();
        stats = new ArrayList<Statistic>();

        teamListeners = new ArrayList<ChangeListener>();
    }


    public static DatabaseManager register(Context c, SyncManager sm)
    {
        instance = new DatabaseManager(c, sm);
        return instance;
    }

    public static DatabaseManager get()
    {
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (mDb != null)
        {
            throw new IllegalStateException("There can only be one! (call reOpen())");
        }

        db.execSQL("CREATE TABLE questions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, qtype CHAR (2), qtext VARCHAR(255))");
        db.execSQL("CREATE TABLE offeredAnswers (qid INTEGER NOT NULL, ansid INTEGER, offered VARCHAR(255))");
        db.execSQL("CREATE TABLE answers (qid INTEGER NOT NULL, team INTEGER NOT NULL, answer VARCHAR(255))");
        db.execSQL("CREATE TABLE teams (id INTEGER PRIMARY KEY NOT NULL, name VARCHAR(255))");
        db.execSQL("CREATE TABLE matches (id INTEGER PRIMARY KEY NOT NULL, red1 INTEGER, red2 INTEGER, " +
                "red3 INTEGER, blue1 INTEGER, blue2 INTEGER, blue3 INTEGER, redScore INTEGER, blueScore INTEGER)");
        db.execSQL("CREATE TABLE scores (mid INTEGER, tid INTEGER, auto INTEGER, teleop INTEGER, fouls INTEGER)");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        //TODO: Implement match downloading from DB. Jebus this might get a little complicated.
    }

    public List<Match> getMatchesForTeam(final Team t)
    {
        if (!matchesInitialized)
        {
            initializeMatches();
            matchesInitialized = true;
        }

        final List<Match> matchedMatches = new ArrayList<Match>();
        for (Match m : matches)
        {
            if (Arrays.asList(m.getTeams()).contains(t))
            {
                matchedMatches.add(m);
            }
        }

        return matchedMatches;
    }

    private Team getTeamForNumber(final int teamNumber) {
        return CollectionUtils.find(teams, new Predicate<Team>() {
            @Override
            public boolean evaluate(Team team) {
                return team.getNumber() == teamNumber;
            }
        });
    }

    private boolean statsInitialized = false;

    private void initializeStatistics() {
        // True for new session.
        List<SyncManager.Operation<Statistic>> statsChanged = sm.syncStatistics(mDb);

        for (SyncManager.Operation<Statistic> so : statsChanged)
        {
            int operation = so.getOperation();

            Statistic s = so.getOperateOn();

            if (operation == SyncManager.Operation.ADDITION)
            {
                ContentValues cv = new ContentValues();

                cv.put("qtype", s.getTypeString());
                cv.put("qtext", s.getDescription());

                mDb.insert("questions", "qtext", cv);
            }
            else if (operation == SyncManager.Operation.DELETION)
            {

            }
        }

        Cursor questionCursor = mDb.query("questions", new String [] {"id", "qtype", "qtext"}, null, null, null,
                null, null, null);

        questionCursor.moveToNext();

        if (!questionCursor.isAfterLast())
        {
            int i = 1;
            do {
                int id = questionCursor.getInt(0);
                String qType = questionCursor.getString(1);
                String qText = questionCursor.getString(2);
                Class<? extends Statistic> statClazz = AbstractStatistic.getStatisticType(qType);

                try {
                    if (Question.class.isAssignableFrom(statClazz))
                    {
                        List<String> offeredAnswers = new ArrayList<String>();

                        Constructor<? extends Statistic> constructor = statClazz.getConstructor(int.class,
                                String.class, String[].class);
                        Cursor offerCursor = mDb.query("offeredAnswers", new String[] {"qid", "ansid", "offered"},
                                "qid=" + (i), null, null, null, null, null);

                        offerCursor.moveToNext();

                        if (!offerCursor.isAfterLast())
                        {
                            do {
                                offeredAnswers.add(offerCursor.getString(2));
                            } while (offerCursor.moveToNext());
                        }

                        Statistic s = constructor.newInstance(id, qText, offeredAnswers.toArray(
                                new String[offeredAnswers.size()]));

                        Cursor answerCursor = mDb.query("answers", null, "qid=" + i, null, null, null, null);

                        answerCursor.moveToNext();

                        if (!answerCursor.isAfterLast())
                        {
                            do {
                                ((Question)s).addAnswer(getTeamForNumber(answerCursor.getInt(1)), answerCursor.getString(2));
                            } while (answerCursor.moveToNext());
                        }

                        stats.add(s);
                    }
                    else
                    {
                        Constructor<? extends Statistic> constructor = statClazz.getConstructor(int.class);
                        stats.add(constructor.newInstance(id));
                    }
                } catch (NoSuchMethodException e) {
                    Log.wtf("ASDB", "Couldn't find constructor which takes a single string for type "
                            + statClazz.getSimpleName() + ".");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                i ++;
            } while (questionCursor.moveToNext());
        }
    }

    public List<Statistic> getStats()
    {
        if (!statsInitialized)
        {
            initializeStatistics();
            statsInitialized = true;
        }

        return stats;
    }


    private boolean teamsInitialized;

    private void initializeTeams()
    {
        List<SyncManager.Operation<Team>> teamsChanged = sm.syncTeams(mDb);

        for (SyncManager.Operation<Team> to : teamsChanged)
        {
            int operation = to.getOperation();

            Team t = to.getOperateOn();

            if (operation == SyncManager.Operation.ADDITION)
            {
                ContentValues cv = new ContentValues();

                cv.put("id", t.getNumber());
                cv.put("name", t.getName());

                mDb.insert("teams", "name", cv);
            }
            else if (operation == SyncManager.Operation.DELETION)
            {
                //TODO: Implement deletion.
            }
        }

        Cursor teamCursor = mDb.query("teams", new String[] {"id", "name"}, null, null, null,
                null, null, null);

        teamCursor.moveToNext();

        if (!teamCursor.isAfterLast())
        {
            do {
                teams.add(new Team(teamCursor.getInt(0), teamCursor.getString(1)));
            } while (teamCursor.moveToNext());
        }
    }

    public List<Team> getTeams()
    {
        if (!teamsInitialized)
        {
            initializeTeams();
            teamsInitialized = true;
        }

        return teams;
    }

    public void addTeam(Team t)
    {
        ContentValues cv = new ContentValues();

        cv.put("id", t.getNumber());
        cv.put("name", t.getName());

        mDb.insert("teams", "name", cv);

        teams.add(t);

        for (ChangeListener cl : teamListeners)
        {
            cl.onChange(this);
        }
    }

    public void addTeamChangedListener(ChangeListener cl)
    {
        teamListeners.add(cl);
    }

    private boolean matchesInitialized = false;

    private void initializeMatches()
    {
        if (!teamsInitialized)
        {
            initializeTeams();
            teamsInitialized = true;
        }

        List<SyncManager.Operation<Match>> matchesChanged = sm.syncMatchesScores(teams, mDb);

        for (SyncManager.Operation<Match> mo : matchesChanged)
        {
            int operation = mo.getOperation();

            Match m = mo.getOperateOn();

            if (operation == SyncManager.Operation.ADDITION)
            {
                ContentValues cv = new ContentValues();

                cv.put("id", m.getNumber());
                cv.put("red1", m.getTeam(Match.Alliance.RED, 1).getNumber());
                cv.put("red2", m.getTeam(Match.Alliance.RED, 2).getNumber());
                cv.put("red3", m.getTeam(Match.Alliance.RED, 3).getNumber());
                cv.put("blue1", m.getTeam(Match.Alliance.BLUE, 1).getNumber());
                cv.put("blue2", m.getTeam(Match.Alliance.BLUE, 2).getNumber());
                cv.put("blue3", m.getTeam(Match.Alliance.BLUE, 3).getNumber());
                cv.put("redScore", m.getActualRedScore());
                cv.put("blueScore", m.getActualBlueScore());


                mDb.insert("matches", "blueScore", cv);
            }
            else if (operation == SyncManager.Operation.DELETION)
            {
                //TODO: Implement deletion.
            }
        }

        Cursor c = mDb.query("matches", new String[] {"id", "red1", "red2", "red3", "blue1",
                "blue2", "blue3", "blueScore", "redScore"},
                null, null, null, null, null);

        c.moveToNext();

        if (!c.isAfterLast())
        {
            do {
                matches.add(new Match(c.getInt(0), getTeamForNumber(c.getInt(1)), getTeamForNumber(c.getInt(2)),
                        getTeamForNumber(c.getInt(3)), getTeamForNumber(c.getInt(4)), getTeamForNumber(c.getInt(5)),
                        getTeamForNumber(c.getInt(6)), c.getInt(7), c.getInt(8)));
            } while (c.moveToNext());
        }
    }

    public List<Match> getMatches()
    {
        // We do a lazy initialization on the ArrayLists...

        if (!matchesInitialized)
        {
            initializeMatches();
            matchesInitialized = true;
        }

        return matches;
    }

    public void reOpen() throws SQLException
    {
        if (mDb == null)
        {
            throw new SQLException("Cannot close a database that hasn't been opened.");
        }

        this.close();
        mDb = this.getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void recordAnswer(Question question, Team t, CharSequence answer) {
        ContentValues cv = new ContentValues();

        cv.put("qid", question.getId());
        cv.put("team", t.getNumber());
        cv.put("answer", answer.toString());

        mDb.insert("answers", "answer", cv);
    }


    public interface ChangeListener {
        public void onChange(DatabaseManager dbm);
    }

    // public class Obama implements ChangeListener { public void onChange(DatabaseManager dbm) { System.out.println("Vote for change!"); } }
}
