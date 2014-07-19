package com.allsaintsrobotics.scouting.survey.customs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allsaintsrobotics.scouting.R;
import com.allsaintsrobotics.scouting.survey.Form;
import com.allsaintsrobotics.scouting.survey.Question;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jack on 30/01/14.
 * This file is a part of the ASTECHZ Scouting Client.
 */
public class TeleopAAForm<T> extends Form<T> {
    private RelativeLayout view = null;

    private int truss = 0;
    private int assists = 1;
    private int catches = 0;
    private int score = 0;

    private Button trussPlus;
    private Button trussMinus;
    private Button catchPlus;
    private Button catchMinus;

    private TextView assistsLabel;
    private TextView scoreLabel;

    public TeleopAAForm(Question<T> q, T t) {
        super(q, t);
    }

    @Override
    public View getAnswerView(Activity c, ViewGroup parent) {
        if (view == null) {
            LayoutInflater li =  (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.view = (RelativeLayout)li.inflate(R.layout.question_teleopaa, null);

            this.assistsLabel = (TextView) view.findViewById(R.id.assist_label);

            assistsLabel.setText(Integer.toString(assists));

            this.scoreLabel = (TextView) view.findViewById(R.id.score_label);

            this.trussPlus = (Button) view.findViewById(R.id.truss_plus);

            trussPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    truss ++;
                    score += 10;

                    trussPlus.setEnabled(false);
                    trussMinus.setEnabled(true);

                    scoreLabel.setText(Integer.toString(score));
                }
            });

            this.trussMinus = (Button) view.findViewById(R.id.truss_minus);

            trussMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (truss > 0) {
                        truss --;
                        trussPlus.setEnabled(true);
                        trussMinus.setEnabled(false);
                    }
                    if (score >= 10) {
                        score -= 10;
                    }
                    else {
                        score = 0;
                    }

                    scoreLabel.setText(Integer.toString(score));
                }
            });

            this.catchPlus = (Button) view.findViewById(R.id.catch_plus);
            this.catchPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    catches ++;
                    score += 10;

                    catchPlus.setEnabled(false);
                    catchMinus.setEnabled(true);

                    scoreLabel.setText(Integer.toString(score));
                }
            });

            this.catchMinus = (Button) view.findViewById(R.id.catch_minus);
            this.catchMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (catches > 0) {
                        catches ++;
                        catchPlus.setEnabled(true);
                        catchMinus.setEnabled(false);
                    }
                    if (score >= 10) {
                        score -= 10;
                    }
                    else {
                        score = 0;
                    }

                    scoreLabel.setText(Integer.toString(score));
                }
            });

            Button assistPlus = (Button) view.findViewById(R.id.assist_plus);
            assistPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (assists < 3) {
                        assists++;
                    }

                    assistsLabel.setText(Integer.toString(assists));
                }
            });

            Button assistMinus = (Button) view.findViewById(R.id.assist_minus);
            assistMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (assists > 1) {
                        assists--;
                    }

                    assistsLabel.setText(Integer.toString(assists));
                }
            });

            Button scorePlus = (Button) view.findViewById(R.id.score_plus);
            scorePlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    score += assists * 10;

                    catchPlus.setEnabled(true);
                    trussPlus.setEnabled(true);
                    catchMinus.setEnabled(false);
                    trussMinus.setEnabled(false);

                    scoreLabel.setText(Integer.toString(score));
                }
            });

            Button scoreMinus = (Button) view.findViewById(R.id.score_minus);
            scoreMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (score > assists * 10) {
                        score -= assists * 10;
                    } else {
                        score = 0;
                    }

                    catchPlus.setEnabled(true);
                    trussPlus.setEnabled(true);
                    catchMinus.setEnabled(false);
                    trussMinus.setEnabled(false);

                    scoreLabel.setText(Integer.toString(score));
                }
            });

            trussMinus.setEnabled(false);
            catchMinus.setEnabled(false);
        }

        String text = question.getAnswer(t);

        if (text != null) {
            try {
                JSONObject data = new JSONObject(text);

                truss = data.getInt("truss");
                catches = data.getInt("catches");
                score = data.getInt("score");

                scoreLabel.setText(Integer.toString(score));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    @Override
    public String getAnswer() {
        try {
            JSONObject json = new JSONObject();

            json.put("truss", truss);
            json.put("catches", catches);
            json.put("score", score);
            json.put("value", score);

            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void setError(String error) {

    }
}
