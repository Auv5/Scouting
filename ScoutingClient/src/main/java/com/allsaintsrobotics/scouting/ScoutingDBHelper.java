package com.allsaintsrobotics.scouting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.models.Team;
import com.allsaintsrobotics.scouting.survey.FormFactory;
import com.allsaintsrobotics.scouting.survey.Question;

import java.util.ArrayList;
import java.util.Collections;
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
    public static final String QUESTION_TEXT = "text";
    public static final String QUESTION_TYPE = "type";
    private static final String QUESTION_CREATE = "CREATE TABLE " + TABLE_QUESTIONS + "(" + QUESTION_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + QUESTION_TEXT + " TEXT, " + QUESTION_TYPE +
            " TEXT" + ");";

    public static final String TABLE_ANSWERS = "answers";
    public static final String ANSWER_ID = "_id";
    public static final String ANSWER_QUESTION = "qid";
    public static final String ANSWER_TEAM = "tid";
    public static final String ANSWER_TEXT = "text";
    private static final String ANSWER_CREATE = "CREATE TABLE " + TABLE_ANSWERS + "(" + ANSWER_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + ANSWER_QUESTION + " INTEGER NOT NULL, " +
            ANSWER_TEAM + " INTEGER NOT NULL, " + ANSWER_TEXT + " TEXT" + ");";

    public static final String TABLE_OFFERS = "offered_answers";
    public static final String OFFER_QUESTION = "qid";
    public static final String OFFER_NUMBER = "offer_order";
    public static final String OFFER_TEXT = "text";
    private static final String OFFER_CREATE = "CREATE TABLE " + TABLE_OFFERS + "(" + OFFER_QUESTION +
            " INTEGER NOT NULL, " + OFFER_NUMBER + " INTEGER NOT NULL, " + OFFER_TEXT + " TEXT" + ");";

    public static final String TABLE_MATCHES = "matches";
    public static final String MATCH_NUMBER = "_id";
    public static final String MATCH_RED = "red";
    public static final String MATCH_BLUE = "blue";
    private static final String MATCH_CREATE = "CREATE TABLE " + TABLE_MATCHES + "(" + MATCH_NUMBER +
            " INTEGER PRIMARY KEY NOT NULL, " + MATCH_RED + " INTEGER NOT NULL, " + MATCH_BLUE +
            " INTEGER NOT NULL" + ");";

    public static final String TABLE_ALLIANCE = "alliances";
    public static final String ALLIANCE_ID = "id";
    public static final String ALLIANCE_TEAM = "tid";
    private static final String ALLIANCE_CREATE = "CREATE TABLE " + TABLE_ALLIANCE + "(" + ALLIANCE_ID +
            " INTEGER NOT NULL, " + ALLIANCE_TEAM + " INTEGER NOT NULL" + ");";

    public static final String TABLE_POINTS = "points";
    public static final String POINTS_MATCH = "mid";
    public static final String POINTS_TEAM = "tid";
    public static final String POINTS_TYPE = "type";
    public static final String POINTS_AMOUNT = "pts";
    private static final String POINTS_CREATE = "CREATE TABLE " + TABLE_POINTS + "(" + POINTS_MATCH +
            " INTEGER NOT NULL, " + POINTS_TEAM + " INTEGER NOT NULL, " + POINTS_TYPE +
            " TEXT, " + POINTS_AMOUNT + " INTEGER NOT NULL" + ");";

    private SQLiteDatabase db;

    private static ScoutingDBHelper instance;

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

        this.db = this.getWritableDatabase();
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL(TEAM_CREATE);
        db.execSQL(QUESTION_CREATE);
        db.execSQL(ANSWER_CREATE);
        db.execSQL(OFFER_CREATE);
        db.execSQL(MATCH_CREATE);
        db.execSQL(ALLIANCE_CREATE);
        db.execSQL(POINTS_CREATE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // BEGIN CONVENIENCE METHODS

    public Cursor getAllTeams() {
        return db.rawQuery("SELECT " + TEAM_NUM + " as _id, " + TEAM_NUM + ", " + TEAM_NAME +
                " FROM " + TABLE_TEAMS, new String[] {});
    }

    public Cursor getAllTeams(String orderBy) {
        return db.rawQuery("SELECT " + TEAM_NUM + " as _id, " + TEAM_NUM + ", " + TEAM_NAME +
                " FROM " + TABLE_TEAMS + " ORDER BY " + orderBy, new String[] {});
    }

    private List<Team> teamCache;

    public List<Team> getTeams() {
        if (teamCache == null) {
            teamCache = new ArrayList<Team>();

            Cursor cursor = db.query(TABLE_TEAMS, new String[] {TEAM_NUM, TEAM_NAME},
                    null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                teamCache.add(Team.fromCursor(cursor));
            }

            return teamCache;
        }
        else {
            return teamCache;
        }
    }

    public void setAnswer(Question question, Team team, String answer) {
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

    public String getAnswer(Question question, Team team) {
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
            throw new IllegalStateException("Cannot have more than one answer for a question...");
        }
    }

    List<Question> questionsCached = null;

    public void invalidateQuestionCache() {
        questionsCached = null;
    }

    public List<Question> getQuestions() {
        if (questionsCached != null)
        {
            return questionsCached;
        }

        List<Question> questions = new ArrayList<Question>();

        Cursor questionCursor = db.query(TABLE_QUESTIONS, null, null, null, null, null, null);

        while (questionCursor.moveToNext()) {
            String label = questionCursor.getString(questionCursor.getColumnIndex(QUESTION_TEXT));

            int qid = questionCursor.getInt(questionCursor.getColumnIndex(QUESTION_ID));

            String[] offers = getOffersForQid(qid);

            String type = questionCursor.getString(questionCursor.getColumnIndex(QUESTION_TYPE));

            FormFactory factory = FormFactory.forId(type);

            factory.setOffers(offers);

            questions.add(new Question(
                    label,
                    factory,
                    qid));
        }

        return this.questionsCached = questions;
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
            if (m.hasTeam(team.getNumber())) {
                matches.add(m);
            }
        }

        return matches;
    }

    public List<Match> getMatches() {
        if (matchCache != null) {
            return matchCache;
        }

        matchCache = new ArrayList<Match>();

        Cursor matchCursor = db.query(TABLE_MATCHES, null, null, null, null, null, null);

        while (matchCursor.moveToNext()) {
            int id = matchCursor.getInt(matchCursor.getColumnIndex(MATCH_NUMBER));

            int redid = matchCursor.getInt(matchCursor.getColumnIndex(MATCH_RED));
            int blueid = matchCursor.getInt(matchCursor.getColumnIndex(MATCH_BLUE));

            Cursor redAllianceCursor = db.query(TABLE_ALLIANCE, null, ALLIANCE_ID + " = ?",
                    new String[] {Integer.toString(redid)}, null, null, null);
            Cursor blueAllianceCursor = db.query(TABLE_ALLIANCE, null, ALLIANCE_ID + " = ?",
                    new String[] {Integer.toString(blueid)}, null, null, null);

            if (redAllianceCursor.getCount() != 3 || blueAllianceCursor.getCount() != 3) {
                throw new IllegalStateException("Must have three teams in an alliance.");
            }

            int[] red = new int[3];
            int[] blue = new int[3];

            int i = 0;

            while (redAllianceCursor.moveToNext() && blueAllianceCursor.moveToNext()) {
                red[i] = redAllianceCursor.getInt(redAllianceCursor.getColumnIndex(ALLIANCE_TEAM));
                blue[i] = blueAllianceCursor.getInt(blueAllianceCursor.getColumnIndex(ALLIANCE_TEAM));

                i++;
            }

            int[] redAuto = new int[3];
            int[] redTeleop = new int[3];
            int[] redSpecial = new int[3];

            int[] blueAuto = new int[3];
            int[] blueTeleop = new int[3];
            int[] blueSpecial = new int[3];

            for (i = 0; i < 3; i ++) {
                redAuto[i] = -1;
                redTeleop[i] = -1;
                redSpecial[i] = -1;

                blueAuto[i] = -1;
                blueTeleop[i] = -1;
                blueSpecial[i] = -1;
            }

            i = 0;

            while (i < red.length && i < blue.length) {
                Cursor redPointsCursor = db.query(TABLE_POINTS, null, POINTS_TEAM + " = ? AND "
                        + POINTS_MATCH + " = ?", new String[] {Integer.toString(red[i]),
                        Integer.toString(id)}, null, null, null);
                Cursor bluePointsCursor = db.query(TABLE_POINTS, null, POINTS_TEAM + " = ? AND "
                        + POINTS_MATCH + " = ?", new String[] {Integer.toString(red[i]),
                        Integer.toString(id)}, null, null, null);

                while (redPointsCursor.moveToNext()) {
                    String redType = redPointsCursor.getString(redPointsCursor.getColumnIndex(POINTS_TYPE));
                    int redValue = redPointsCursor.getInt(redPointsCursor.getColumnIndex(POINTS_AMOUNT));

                    if (redType.equalsIgnoreCase("auto")) {
                        redAuto[i] = redValue;
                    }
                    else if (redType.equalsIgnoreCase("teleop")) {
                        redTeleop[i] = redValue;
                    }
                    else if (redType.equalsIgnoreCase("special")) {
                        redSpecial[i] = redValue;
                    }
                }

                while (bluePointsCursor.moveToNext()) {
                    String blueType = bluePointsCursor.getString(bluePointsCursor.getColumnIndex(POINTS_TYPE));
                    int blueValue = bluePointsCursor.getInt(bluePointsCursor.getColumnIndex(POINTS_AMOUNT));

                    if (blueType.equalsIgnoreCase("auto")) {
                        blueAuto[i] = blueValue;
                    }
                    else if (blueType.equalsIgnoreCase("teleop")) {
                        blueTeleop[i] = blueValue;
                    }
                    else if (blueType.equalsIgnoreCase("special")) {
                        blueSpecial[i] = blueValue;
                    }
                }

                i ++;
            }

            Match m = new Match(id, blue, red, blueAuto, redAuto, blueTeleop, redTeleop,
                    blueSpecial, redSpecial);

            matchCache.add(m);
        }

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

    // END CONVENIENCE METHODS
}
