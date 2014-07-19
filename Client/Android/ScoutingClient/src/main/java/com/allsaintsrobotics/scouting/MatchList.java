package com.allsaintsrobotics.scouting;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.allsaintsrobotics.scouting.adapters.MatchAdapter;
import com.allsaintsrobotics.scouting.models.Match;

import java.util.List;

/**
 * Created by jack on 12/3/13.
 * This file is a part of the ASTECHZ Scouting app.
 */
public class MatchList extends Fragment {
    private ListView lv;
    private MatchAdapter adapter;

    public MatchList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.match_list, container, false);

        this.lv = (ListView) v.findViewById(R.id.match_genericlist);

        new MatchPopulateTask().execute();

        return v;
    }

    public void invalidate() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void populateMatchList(List<Match> matches) {
        this.adapter = new MatchAdapter(getActivity(), null,
                matches);
        this.lv.setAdapter(adapter);
    }

    private class MatchPopulateTask extends AsyncTask<Void, Void, List<Match>> {
        @Override
        protected List<Match> doInBackground(Void... params) {
            return ScoutingDBHelper.getInstance().getMatches();
        }

        @Override
        protected void onPostExecute(List<Match> matches) {
            populateMatchList(matches);
        }
    }
}
