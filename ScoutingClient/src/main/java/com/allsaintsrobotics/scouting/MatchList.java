package com.allsaintsrobotics.scouting;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.allsaintsrobotics.scouting.adapters.MatchAdapter;

/**
 * Created by jack on 12/3/13.
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

        this.adapter = new MatchAdapter(getActivity(), null,
                ScoutingDBHelper.getInstance().getMatches());

        this.lv.setAdapter(adapter);

        return v;
    }

    public void invalidate() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
