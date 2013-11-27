package com.allsaintsrobotics.scouting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.allsaintsrobotics.scouting.models.Team;
import com.allsaintsrobotics.scouting.survey.FormFactory;
import com.allsaintsrobotics.scouting.survey.Question;

import java.util.ArrayList;
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

    public void setAnswer(Question question, Team team, String answer) {
        ContentValues cv = new ContentValues();

        cv.put(ANSWER_QUESTION, question.getId());
        cv.put(ANSWER_TEAM, team.getNumber());
        cv.put(ANSWER_TEXT, answer);

        if (this.getAnswer(question, team) == null) {
            db.insert(TABLE_ANSWERS, ANSWER_TEXT, cv);
        }
        else {
            db.update(TABLE_ANSWERS, cv, ANSWER_QUESTION + "=? AND " + ANSWER_TEAM + "=?",
                    new String[] {Integer.toString(question.getId()), Integer.toString(team.getNumber())});
        }
    }

    public String getAnswer(Question question, Team team) {
        Cursor c = db.query(TABLE_ANSWERS, new String[] {ANSWER_TEXT}, ANSWER_QUESTION + "=? AND "
                + ANSWER_TEAM + "=?", new String[] {Integer.toString(question.getId()),
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
        Cursor offerCursor = db.query(TABLE_OFFERS, null, OFFER_QUESTION + "=?", new String[] {Integer.toString(qid)},
                null, null, OFFER_NUMBER);

        List<String> offers = new ArrayList<String>();

        while (offerCursor.moveToNext()) {
            offers.add(offerCursor.getString(offerCursor.getColumnIndex(OFFER_TEXT)));
        }

        return offers.toArray(new String[offers.size()]);
    }

    // END CONVENIENCE METHODS
}
