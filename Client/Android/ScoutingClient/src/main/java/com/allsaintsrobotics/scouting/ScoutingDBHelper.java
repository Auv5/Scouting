package com.allsaintsrobotics.scouting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.models.Team;
import com.allsaintsrobotics.scouting.survey.QCustomFactory;
import com.allsaintsrobotics.scouting.survey.TeamQuestion;
import com.allsaintsrobotics.scouting.survey.MatchQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jack on 11/24/13.
 */
public class ScoutingDBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "scouting.db";
    public static final int VERSION = 1;

    public static final String TABLE_TEAMS = "teams";
    public static final String TEAM_NUM = "number";
    public static final String TEAM_NAME = "nickname";
    private static final String TEAM_CREATE = "CREATE TABLE " + TABLE_TEAMS + "(" + TEAM_NUM +
            " INTEGER PRIMARY KEY NOT NULL, " + TEAM_NAME + " TEXT" + ");";

    public static final String TABLE_QUESTIONS = "questions";
    public static final String QUESTION_ID = "_id";
    public static final String QUESTION_STRID = "strid";
    public static final String QUESTION_TEXT = "text";
    public static final String QUESTION_TYPE = "type";
    private static final String QUESTION_CREATE = "CREATE TABLE " + TABLE_QUESTIONS + "(" + QUESTION_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + QUESTION_STRID + " TEXT, " + QUESTION_TEXT + " TEXT, " + QUESTION_TYPE +
            " TEXT" + ");";

    public static final String TABLE_ANSWERS = "answers";
    public static final String ANSWER_ID = "_id";
    public static final String ANSWER_QUESTION = "qid";
    public static final String ANSWER_TEAM = "tid";
    public static final String ANSWER_MATCH = "mid";
    public static final String ANSWER_TEXT = "text";
    private static final String ANSWER_CREATE = "CREATE TABLE " + TABLE_ANSWERS + "(" + ANSWER_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + ANSWER_QUESTION + " INTEGER NOT NULL, " +
            ANSWER_TEAM + " INTEGER NOT NULL, " + ANSWER_MATCH + " INTEGER, " + ANSWER_TEXT + " TEXT" + ");";

    public static final String TABLE_OFFERS = "offered_answers";
    public static final String OFFER_QUESTION = "qid";
    public static final String OFFER_NUMBER = "offer_order";
    public static final String OFFER_TEXT = "text";
    private static final String OFFER_CREATE = "CREATE TABLE " + TABLE_OFFERS + "(" + OFFER_QUESTION +
            " INTEGER NOT NULL, " + OFFER_NUMBER + " INTEGER NOT NULL, " + OFFER_TEXT + " TEXT" + ");";

    public static final String TABLE_MATCHES = "matches";
    public static final String MATCH_NUMBER = "_id";
    public static final String MATCH_RED1 = "red1";
    public static final String MATCH_RED2 = "red2";
    public static final String MATCH_RED3 = "red3";
    public static final String MATCH_BLUE1 = "blue1";
    public static final String MATCH_BLUE2 = "blue2";
    public static final String MATCH_BLUE3 = "blue3";
    public static final String MATCH_SCOUT = "scout";
    
    private static final String MATCH_CREATE = "CREATE TABLE " + TABLE_MATCHES + "(" + MATCH_NUMBER +
            " INTEGER PRIMARY KEY NOT NULL, " + MATCH_RED1 + " INTEGER NOT NULL, " + MATCH_RED2 +
            " INTEGER NOT NULL, " + MATCH_RED3 + " INTEGER NOT NULL, " + MATCH_BLUE1 + " INTEGER NOT NULL, "
            + MATCH_BLUE2 + " INTEGER NOT NULL, " + MATCH_BLUE3 + " INTEGER NOT NULL, " + MATCH_SCOUT +
            " INTEGER NOT NULL" + ");";

    public static final String TABLE_SCOUTMETA = "scout_meta";
    public static final String SCOUTMETA_OPTION = "option";
    public static final String SCOUTMETA_VALUE = "value";
    private static final String SCOUTMETA_CREATE = "CREATE TABLE " + TABLE_SCOUTMETA + "(" +
            SCOUTMETA_OPTION + " TEXT, " + SCOUTMETA_VALUE + " TEXT" + ");";

    private SQLiteDatabase db;

    private static ScoutingDBHelper instance;

    private int id;

    public static ScoutingDBHelper makeInstance(Context context) {
        if (instance != null)
        {
            throw new IllegalStateException("Cannot make more than one scouting database.");
        }

        return instance = new ScoutingDBHelper(context);
    }

    public static ScoutingDBHelper getInstance() {
        if (instance == null)
        {
            throw new IllegalStateException("Database not instantiated. Call makeInstance(context)");
        }

        return instance;
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    private ScoutingDBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        this.id = -2;
        this.db = this.getWritableDatabase();
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL(TEAM_CREATE);
        db.execSQL(QUESTION_CREATE);
        db.execSQL(ANSWER_CREATE);
        db.execSQL(OFFER_CREATE);
        db.execSQL(MATCH_CREATE);
        db.execSQL(SCOUTMETA_CREATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // BEGIN CONVENIENCE METHODS

    public void clearSyncData() {
        db.execSQL("DELETE FROM " + TABLE_TEAMS);
        db.execSQL("DELETE FROM " + TABLE_MATCHES);
        db.execSQL("DELETE FROM " + TABLE_ANSWERS);
        db.execSQL("DELETE FROM " + TABLE_QUESTIONS);
        db.execSQL("DELETE FROM " + TABLE_SCOUTMETA);
        db.execSQL("DELETE FROM " + TABLE_OFFERS);
        this.id = -1;
    }

    private List<Team> teamCache;

    Comparator<Team> teamComparator = new Comparator<Team>() {
        @Override
        public int compare(Team lhs, Team rhs) {
            return lhs.getNumber() - rhs.getNumber();
        }
    };

    private void addToTeamCache(Team t) {
        teamCache.add(t);
    }

    public void sortTeams() {
        Collections.sort(teamCache, teamComparator);
    }

    public List<Team> getTeams() {
        if (teamCache == null) {
            teamCache = new ArrayList<Team>();

            Cursor cursor = db.query(TABLE_TEAMS, new String[] {TEAM_NUM, TEAM_NAME},
                    null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                teamCache.add(Team.fromCursor(cursor));
            }

            Collections.sort(teamCache, teamComparator);

            return teamCache;
        }
        else {
            return teamCache;
        }
    }

    public void setAnswer(TeamQuestion question, Team team, String answer) {
        ContentValues cv = new ContentValues();

        cv.put(ANSWER_QUESTION, question.getId());
        cv.put(ANSWER_TEAM, team.getNumber());
        cv.put(ANSWER_TEXT, answer);

        if (this.getAnswer(question, team) == null) {
            db.insert(TABLE_ANSWERS, ANSWER_TEXT, cv);
        }
        else {
            db.update(TABLE_ANSWERS, cv, ANSWER_QUESTION + " = ? AND " + ANSWER_TEAM + " = ?",
                    new String[] {Integer.toString(question.getId()), Integer.toString(team.getNumber())});
        }
    }

    public void setAnswer(MatchQuestion question, Match match, String answer) {
        ContentValues cv = new ContentValues();

        cv.put(ANSWER_QUESTION, question.getId());
        cv.put(ANSWER_TEAM, match.getScout());
        cv.put(ANSWER_MATCH, match.getNumber());
        cv.put(ANSWER_TEXT, answer);

        if (this.getAnswer(question, match) == null) {
            db.insert(TABLE_ANSWERS, ANSWER_TEXT, cv);
        }
        else {
            db.update(TABLE_ANSWERS, cv, ANSWER_QUESTION + " = ? AND " + ANSWER_TEAM + " = ? AND " + ANSWER_MATCH + " = ?",
                    new String[] {Integer.toString(question.getId()), Integer.toString(match.getScout()), Integer.toString(match.getNumber())});
        }
    }

    public String getAnswer(TeamQuestion question, Team team) {
        Cursor c = db.query(TABLE_ANSWERS, new String[] {ANSWER_TEXT}, ANSWER_QUESTION + " = ? AND "
                + ANSWER_TEAM + " = ?", new String[] {Integer.toString(question.getId()),
                Integer.toString(team.getNumber())}, null, null, null);

        int count = c.getCount();

        if (count == 0)
        {
            return null;
        }
        else if (count == 1)
        {
            c.moveToNext();
            // We only retrieved the answer above.
            return c.getString(0);
        }
        else
        {
            throw new IllegalStateException("Cannot have more than one answer for a question (perhaps this is a match question? You called the team question method)...");
        }
    }
    
    public String getAnswer(MatchQuestion question, Match match) {
        Cursor c = db.query(TABLE_ANSWERS, new String[] {ANSWER_TEXT}, ANSWER_QUESTION + " = ? AND " + ANSWER_TEAM + " = ? AND " + ANSWER_MATCH + " = ?",
                new String[] {Integer.toString(question.getId()), Integer.toString(match.getScout()), Integer.toString(match.getNumber())}, null, null, null);

        int count = c.getCount();

        if (count == 0) {
            return null;
        }
        else if (count == 1) {
            c.moveToNext();
            return c.getString(0);
        }
        else {
            throw new IllegalStateException("Cannot have more than one answer for a match question.");
        }
    }

    List<TeamQuestion> teamQuestionCache = null;

    public TeamQuestion getTeamQuestion(int id) {
        for (TeamQuestion tq : teamQuestionCache) {
            if (tq.getId() == id) {
                return tq;
            }
        }

        return null;
    }

    public List<TeamQuestion> getTeamQuestions() {
        if (teamQuestionCache != null)
        {
            return teamQuestionCache;
        }

        teamQuestionCache = new ArrayList<TeamQuestion>();

        Cursor questionCursor = db.query(TABLE_QUESTIONS, null, QUESTION_TYPE + " NOT LIKE 'm\\_%' ESCAPE '\\'", null, null, null, null);
        
        Log.d("ScoutingClient", "Count: " + questionCursor.getCount());

        while (questionCursor.moveToNext()) {
            String label = questionCursor.getString(questionCursor.getColumnIndex(QUESTION_TEXT));

            int qid = questionCursor.getInt(questionCursor.getColumnIndex(QUESTION_ID));

            String[] offers = getOffersForQid(qid);

            String type = questionCursor.getString(questionCursor.getColumnIndex(QUESTION_TYPE));

            QCustomFactory<Team> factory = (QCustomFactory<Team>) QCustomFactory.forId(type);

            teamQuestionCache.add(new TeamQuestion(
                    label,
                    factory,
                    qid,
                    offers));
        }

        return teamQuestionCache;
    }

    List<MatchQuestion> matchQuestionCache = null;
    
    public MatchQuestion getMatchQuestion(int id) {
        for (MatchQuestion mq : matchQuestionCache) {
            if (mq.getId() == id) {
                return  mq;
            }
        }
        return null;
    }

    public List<MatchQuestion> getMatchQuestions() {
        if (matchQuestionCache != null) {
            return matchQuestionCache;
        }

        matchQuestionCache = new ArrayList<MatchQuestion>();

        Cursor questionCursor = db.query(TABLE_QUESTIONS, null, QUESTION_TYPE + " LIKE 'm\\_%' ESCAPE '\\'", null, null, null, null);

        Log.d("MatchQuestionRead", "Count: " + questionCursor.getCount());

        while (questionCursor.moveToNext()) {
            String label = questionCursor.getString(questionCursor.getColumnIndex(QUESTION_TEXT));

            int qid = questionCursor.getInt(questionCursor.getColumnIndex(QUESTION_ID));

            String[] offers = getOffersForQid(qid);

            String type = questionCursor.getString(questionCursor.getColumnIndex(QUESTION_TYPE));

            QCustomFactory<Match> factory = (QCustomFactory<Match>) QCustomFactory.forId(type);

            matchQuestionCache.add(new MatchQuestion(
                    label,
                    factory,
                    qid,
                    offers));
        }
        
        return matchQuestionCache;
    }

    public void addQuestion(int qid, String label, String type, String[] offers) {
        ContentValues cv = new ContentValues();

        cv.put(QUESTION_TEXT, label);
        cv.put(QUESTION_ID, qid);
        cv.put(QUESTION_TYPE, type);

        db.insert(TABLE_QUESTIONS, null, cv);

        for (int i = 0; i < offers.length; i ++) {
            ContentValues offerCv = new ContentValues();

            offerCv.put(OFFER_NUMBER, i+1);
            offerCv.put(OFFER_QUESTION, qid);
            offerCv.put(OFFER_TEXT, offers[i]);

            db.insert(TABLE_OFFERS, null, offerCv);
        }
    }

    private Comparator<TeamQuestion> questionComparator = new Comparator<TeamQuestion>() {
        @Override
        public int compare(TeamQuestion lhs, TeamQuestion rhs) {
            return lhs.getId() - rhs.getId();
        }
    };

    public void sortQuestions() {
        if (teamQuestionCache == null) {
            getTeamQuestions();
        }
        Collections.sort(teamQuestionCache, questionComparator);
    }

    private String[] getOffersForQid(int qid) {
        // Sort such that we order the offers as intended.
        Cursor offerCursor = db.query(TABLE_OFFERS, null, OFFER_QUESTION + " = ?", new String[] {Integer.toString(qid)},
                null, null, OFFER_NUMBER);

        List<String> offers = new ArrayList<String>();

        while (offerCursor.moveToNext()) {
            offers.add(offerCursor.getString(offerCursor.getColumnIndex(OFFER_TEXT)));
        }

        return offers.toArray(new String[offers.size()]);
    }

    List<Match> matchCache;

    public List<Match> getMatches(Team team) {
        List<Match> matches = new ArrayList<Match>();
        List<Match> allMatches = getMatches();

        for (Match m : allMatches) {
            if (m.getScout() == team.getNumber()) {
                matches.add(m);
            }
        }

        return matches;
    }

    Comparator<Match> matchComparator = new Comparator<Match>() {
        @Override
        public int compare(Match lhs, Match rhs) {
            return lhs.getNumber() - rhs.getNumber();
        }
    };

    public List<Match> getMatches() {
        if (matchCache != null) {
            return matchCache;
        }

        matchCache = new ArrayList<Match>();

        Cursor matchCursor = db.query(TABLE_MATCHES, null, null, null, null, null, null);

        while (matchCursor.moveToNext()) {
            int id = matchCursor.getInt(matchCursor.getColumnIndex(MATCH_NUMBER));
            int scout = matchCursor.getInt(matchCursor.getColumnIndex(MATCH_SCOUT));

            int[] red = new int[3];
            int[] blue = new int[3];

            for (int i = 0; i < 3; i ++) {
                red[i] = matchCursor.getInt(matchCursor.getColumnIndex("red" + (i+1)));
                blue[i] = matchCursor.getInt(matchCursor.getColumnIndex("blue" + (i+1)));
            }

            Match m = new Match(id, blue, red, scout);

            matchCache.add(m);
        }

        Collections.sort(matchCache, matchComparator);

        return matchCache;
    }

    private Team getTeam(int number) {
        for (Team t : getTeams()) {
            if (t.getNumber() == number) {
                return t;
            }
        }
        return null;
    }

    public void addMatch(int matchId, int scout, int[] red, int[] blue) {
        if (matchCache == null) {
            getMatches();
        }

        ContentValues matchCv = new ContentValues();

        matchCv.put(MATCH_NUMBER, matchId);

        matchCv.put(MATCH_SCOUT, scout);

        matchCv.put(MATCH_BLUE1, blue[0]);
        matchCv.put(MATCH_BLUE2, blue[1]);
        matchCv.put(MATCH_BLUE3, blue[2]);

        matchCv.put(MATCH_RED1, red[0]);
        matchCv.put(MATCH_RED2, red[1]);
        matchCv.put(MATCH_RED3, red[2]);

        db.insert(TABLE_MATCHES, null, matchCv);

        matchCache.add(new Match(matchId, red, blue, scout));
    }

    public void sortMatches() {
        Collections.sort(matchCache, matchComparator);
    }

    public void addTeam(int teamNum, String teamNickname) {
        ContentValues cv = new ContentValues();

        cv.put(TEAM_NUM, teamNum);
        cv.put(TEAM_NAME, teamNickname);

        db.insert(TABLE_TEAMS, null, cv);

        addToTeamCache(new Team(teamNum, teamNickname));
    }

    public void setId(int id) {
        this.id = id;

        ContentValues cv = new ContentValues();
        cv.put(SCOUTMETA_OPTION, "id");
        cv.put(SCOUTMETA_VALUE, Integer.toString(id));

        db.insert(TABLE_SCOUTMETA, null, cv);
    }

    public int getId() {
        // -2 is the magic for haven't checked DB yet.
        if (id == -2) {
            // Get from DB or return -1 if no DB value.
            Cursor c = db.query(TABLE_SCOUTMETA, null, SCOUTMETA_OPTION + "='id'",
                    null, null, null, null);

            if (c.getCount() == 1) {
                c.moveToNext();
                id = Integer.valueOf(c.getString(c.getColumnIndex(SCOUTMETA_VALUE)));
                return id;
            }
            else if (c.getCount() == 0) {
                id = -1;
                return id;
            }
            else {
                Log.e(getClass().getName(), "Cannot have more than one meta-ID.");
                throw new IllegalStateException("Cannot have more than one meta-ID.");
            }
        }

        return id;
    }

    // END CONVENIENCE METHODS
}
