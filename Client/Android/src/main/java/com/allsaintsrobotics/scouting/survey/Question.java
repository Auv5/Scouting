package com.allsaintsrobotics.scouting.survey;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allsaintsrobotics.scouting.ScoutingDBHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jack on 11/24/13.
 */
public abstract class Question<T> {
    private FormFactory<T> factory;
    private TextView tv;

    private int id;
    private String label;
    private String[] offers;

    private static final String TAG = "Question";

    private Map<T, String> cache;

    public Question(String label, FormFactory<T> factory, int id, String[] offers) {
        this.factory = factory;
        this.id = id;
        this.label = label;
        this.offers = offers;

        cache = new HashMap<T, String>();
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

    public String getAnswer(T t) {
        // Call internal method.
        return read(t);
    }
    
    // Marked final so that people don't accidentally override this method instead of dbRead().
    private final String read(T t) {
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

    protected abstract String getDefaultPrompt();
    
    public View getValueView(T t, Context c) {
        String val = this.getAnswer(t);

        if (tv == null) {
            tv = new TextView(c);
        }

        tv.setText(val == null ? getDefaultPrompt() : val);

        if (tv.getParent() != null) {
            ((ViewGroup)tv.getParent()).removeView(tv);
        }

        return tv;
    }
    
    public String getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }
}
