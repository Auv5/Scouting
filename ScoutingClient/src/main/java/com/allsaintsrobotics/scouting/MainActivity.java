package com.allsaintsrobotics.scouting;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jack on 12/2/13.
 */
public class MainActivity extends Activity {
    TeamList teamList;
    private MatchList matchList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setTitle("Scouting Client");

        ActionBar.Tab teamListTab = actionBar.newTab().setText("Teams");
        ActionBar.Tab matchTab = actionBar.newTab().setText("Matches");

        teamList = new TeamList();
        matchList = new MatchList();

        teamListTab.setTabListener(new MainActivity.TabListener(teamList));
        matchTab.setTabListener(new MainActivity.TabListener(matchList));

        actionBar.addTab(teamListTab);
        actionBar.addTab(matchTab);

        if (savedInstanceState != null && savedInstanceState.containsKey("tabstate")) {
            actionBar.setSelectedNavigationItem(
                    savedInstanceState.getInt("tabstate"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tabstate", getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.team_list, menu);

        return true;
    }

    private void invalidateData() {
        teamList.invalidate();
        matchList.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_opensettings:
                Intent i = new Intent();
                i.setClass(this, Preferences.class);

                startActivityForResult(i, 0);
                break;
            case R.id.action_syncdata:
                if (ScoutingDBHelper.getInstance().getId() == -1) {
                    new SyncDataTask().execute();
                }
                else {
                    Toast.makeText(this, "Data already synced. Clear scouting data first.", Toast.LENGTH_LONG).
                            show();
                }
                break;
            case R.id.action_clearscoutdata:
                ScoutingDBHelper.getInstance().clearSyncData();
                invalidateData();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class SyncDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
        }

        private JSONObject getRegisterJson() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

            String baseaddr = prefs.getString("server_addr", "failure");

            HttpClient client = new DefaultHttpClient();

            HttpGet request = new HttpGet("http://" + baseaddr +
                    ":8000/api/register");

            try {
                HttpResponse response = client.execute(request);

                StatusLine sl = response.getStatusLine();
                int statusCode = sl.getStatusCode();

                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(content));

                    StringBuilder jsonText = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        jsonText.append(line);
                    }

                    return new JSONObject(jsonText.toString());
                }
                else {
                    Log.e(SyncDataTask.class.getName(), "HTTP " + statusCode +
                            " when registering.");
                    return null;
                }
            } catch (IOException e) {
                Log.e(SyncDataTask.class.getName(), "Failed to download file: " + e.getMessage());
                return null;
            } catch (JSONException e) {
                Log.e(SyncDataTask.class.getName(), "JSON error: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject jsObj = getRegisterJson();

            try {
                if (jsObj == null) {
                    return false;
                }
                ScoutingDBHelper.getInstance().setId(jsObj.getInt("id"));

                JSONArray matches = jsObj.getJSONArray("matches");
                long t1 = System.currentTimeMillis();

                for (int i = 0; i < matches.length(); i ++) {
                    SyncHelper.addMatchFromJson(matches.getJSONObject(i));
                }

                ScoutingDBHelper.getInstance().sortMatches();

                long dt = System.currentTimeMillis() - t1;

                Log.d(getClass().getName(), "Time to load matches from JSON: " + dt);

                JSONArray teams = jsObj.getJSONArray("teams");

                for (int i = 0; i < teams.length(); i ++) {
                    SyncHelper.addTeamFromJson(teams.getJSONArray(i));
                }

                JSONArray questions = jsObj.getJSONArray("questions");

                for (int i = 0; i < questions.length(); i ++) {
                    SyncHelper.addQuestionFromJson(questions.getJSONObject(i));
                }

                ScoutingDBHelper.getInstance().sortQuestions();

                return true;
            } catch (JSONException e) {
                Log.e(SyncDataTask.class.getName(), "Malformed registration.");
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                invalidateData();
            }
            else {
                Toast.makeText(MainActivity.this, "Could not connect to scouting server. Check settings.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class TabListener implements ActionBar.TabListener {
        private Fragment fragment;

        public TabListener(Fragment f) {
            this.fragment = f;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.main_fragcont, fragment);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }
    }
}