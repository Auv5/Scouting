package com.allsaintsrobotics.scouting;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

public class TeamDetail extends Activity {
    private Team t;

    ListView matchStats;
    ArrayAdapter<Match> matchStatsAdapter;
    ListView genStats;
    ArrayAdapter<Statistic> genStatsAdapter;

    LinearLayout matchStatsGroup;
    LinearLayout genStatsGroup;
    private RelativeLayout matchStatsTvButt;
    private RelativeLayout genStatsTvButt;
    private LinearLayout root;

    private void registerStatistics()
    {
        AbstractStatistic.registerStat(TeamNumberStatistic.class, "ts");
        AbstractStatistic.registerStat(MultipleChoice.class, "mc");
        AbstractStatistic.registerStat(FreeResponse.class, "fr");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teamdetail);
        // Show the Up button in the action bar.
        setupActionBar();

        registerStatistics();

        matchStats = (ListView) findViewById(R.id.match_stats);

        if (t == null)
        {
            t = getIntent().getParcelableExtra("team");
        }

        genStatsGroup = (LinearLayout)findViewById(R.id.gen_stats_lv_tv);
        genStatsTvButt = (RelativeLayout)findViewById(R.id.gen_stats_enc);

        matchStatsGroup = (LinearLayout)findViewById(R.id.match_stats_lv_tv);
        matchStatsTvButt = (RelativeLayout)findViewById(R.id.match_stats_enc);

        matchStatsAdapter = new MatchAdapter(t, this, R.layout.listitem_match);

        matchStats.setAdapter(matchStatsAdapter);

        genStats = (ListView) findViewById(R.id.gen_stats);

        List<Statistic> stats = DatabaseManager.get().getStats();

        genStatsAdapter = new StatisticAdapter(t, this, R.layout.listitem_qanda, stats);

        genStats.setAdapter(genStatsAdapter);

        ImageButton editStats = (ImageButton) findViewById(R.id.edit_gen_stats);

        root = (LinearLayout) findViewById(R.id.root);


        editStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TeamDetail.this, ScoutEdit.class);

                i.putExtra("team", t);

                startActivity(i);
            }
        });

        genStatsTvButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matchStatsGroup.getVisibility() == View.VISIBLE)
                {
                    int count = root.getChildCount();

                    for (int i = 0; i < count; i ++)
                    {
                        View subView = root.getChildAt(i);
                        if (subView != null && !subView.equals(genStatsGroup))
                        {
                            subView.setVisibility(View.GONE);
                        }
                    }
                }
                else if (matchStatsGroup.getVisibility() == View.GONE)
                {
                    int count = root.getChildCount();

                    for (int i = 0; i < count; i ++)
                    {
                        View subView = root.getChildAt(i);
                        if (subView != null && !subView.equals(genStatsGroup))
                        {
                            subView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        matchStatsTvButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (genStatsGroup.getVisibility() == View.VISIBLE)
                {
                    int count = root.getChildCount();

                    for (int i = 0; i < count; i ++)
                    {
                        View subView = root.getChildAt(i);
                        if (subView != null && !subView.equals(matchStatsGroup))
                        {
                            subView.setVisibility(View.GONE);
                        }
                    }
                }
                else if (genStatsGroup.getVisibility() == View.GONE)
                {
                    int count = root.getChildCount();

                    for (int i = 0; i < count; i ++)
                    {
                        View subView = root.getChildAt(i);
                        if (subView != null && !subView.equals(matchStatsGroup))
                        {
                            subView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus)
        {
            matchStatsAdapter.notifyDataSetChanged();
            genStatsAdapter.notifyDataSetChanged();
        }
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.team_detail, menu);
        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
