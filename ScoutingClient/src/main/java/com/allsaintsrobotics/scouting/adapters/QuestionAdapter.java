package com.allsaintsrobotics.scouting.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.allsaintsrobotics.scouting.R;
import com.allsaintsrobotics.scouting.survey.Question;

import java.util.List;

/**
 * Created by jack on 11/25/13.
 */
public class QuestionAdapter extends ArrayAdapter<Question> {
    public QuestionAdapter(Context context, List<Question> questions) {
        super(context, R.layout.listitem_pitstats, questions);
    }


}
