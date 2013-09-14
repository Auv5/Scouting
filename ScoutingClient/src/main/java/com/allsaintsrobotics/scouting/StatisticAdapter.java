package com.allsaintsrobotics.scouting;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created on: 8/4/13.
 */
public class StatisticAdapter extends DatabaseLinkedAdapter<Statistic> {
    Team t;
    private int id;
    private Context c;
    private List<Statistic> objects;


    public StatisticAdapter(Team t, Context context, int resource, List<Statistic> objects) {
        super(context, resource, objects);
        this.t = t;
        this.id = resource;
        this.c = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        QAndAFieldHelper qafh = null;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity)c).getLayoutInflater();
            row = inflater.inflate(R.layout.listitem_qanda, parent, false);

            qafh = new QAndAFieldHelper();

            qafh.question = (TextView)row.findViewById(R.id.li_question);
            qafh.answer = (LinearLayout)row.findViewById(R.id.li_answer);

            row.setTag(qafh);
        }
        else
        {
            qafh = (QAndAFieldHelper)row.getTag();
        }

        if (qafh.answer.getChildCount() > 0)
        {
            qafh.answer.removeAllViews();
        }

        qafh.question.setText(objects.get(position).getDescription());
        qafh.answer.addView(objects.get(position).makeView(c, t));

        return row;
    }

    private class QAndAFieldHelper
    {
        TextView question;
        LinearLayout answer;
    }
}
