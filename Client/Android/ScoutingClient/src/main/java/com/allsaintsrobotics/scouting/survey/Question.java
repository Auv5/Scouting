package com.allsaintsrobotics.scouting.survey;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jack on 11/24/13.
 * This file is a part of the ASTECHZ Scouting app.
 */
public abstract class Question<T> {
    private final QCustomFactory<T> factory;
    private TextView tv;

    private final int id;
    private final String label;
    private final String[] offers;
    private final boolean optional;

    private static final String TAG = "Question";

    private final Map<T, String> cache;

    private Question(String label, QCustomFactory<T> factory, int id, String[] offers, boolean optional) {
        this.factory = factory;
        this.id = id;
        this.label = label;
        this.offers = offers;

        this.optional = optional;

        cache = new HashMap<T, String>();
    }

    Question(String label, QCustomFactory<T> factory, int id, String[] offers) {
        this(label, factory, id, offers, false);
    }

    public boolean isOptional() {
        return optional;
    }

    public Form<T> getForm(T t) {
        return factory.getForm(this, t);
    }

    public String[] getOffers() {
        return offers;
    }

    void cacheUpdate(T t, String answer) {
        cache.put(t, answer);
    }

    public JSONObject getJSON(T t) {
        JSONObject json = factory.getJSON(this, t);

        try {
            json.put("id", id);
        } catch (JSONException e) {
            return null;
        }

        return json;
    }

    public String getAnswer(T t) {
        // Call internal method.
        return read(t);
    }
    
    // Marked final so that people don't accidentally override this method instead of dbRead().
    private String read(T t) {
        if (cache.containsKey(t)) {
            return cache.get(t);
        }
        else {
            String value = this.dbRead(t);
            
            // TODO: This will put null values into the cache. Is this okay?
            this.cacheUpdate(t, value);

            return value;
        }
    }

    // Wrapper around internal dbWrite() which updates the cache.
    public final void write(T t, String value) {
        this.dbWrite(t, value);

        this.cacheUpdate(t, value);
    }

    protected abstract String dbRead(T t);

    protected abstract void dbWrite(T t, String value);
    
    public View getValueView(T t, Context c) {
        return factory.getValueView(this, t, c);
    }
    
    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }
}
