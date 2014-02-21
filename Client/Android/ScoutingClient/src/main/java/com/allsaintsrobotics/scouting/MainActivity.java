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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.models.Team;
import com.allsaintsrobotics.scouting.survey.MatchQuestion;
import com.allsaintsrobotics.scouting.survey.Question;
import com.allsaintsrobotics.scouting.survey.TeamQuestion;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack on 12/2/13.
 */
public class MainActivity extends Activity {
    TeamList teamList;
    private MatchList matchList;
    private boolean execTask = false;

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
                    new DownloadDataTask().execute();
                }
                else {
                    new UploadDataTask().execute();
                }
                break;
            case R.id.action_clearscoutdata:
                ScoutingDBHelper.getInstance().clearSyncData();
                invalidateData();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadDataTask extends AsyncTask<Void, Void, Boolean> {
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
                    Log.e(DownloadDataTask.class.getName(), "HTTP " + statusCode +
                            " when registering.");
                    return null;
                }
            } catch (IOException e) {
                Log.e(DownloadDataTask.class.getName(), "Failed to download file: " + e.getMessage());
                return null;
            } catch (JSONException e) {
                Log.e(DownloadDataTask.class.getName(), "JSON error: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (execTask) {
                return null;
            }

            execTask = true;

            long t0 = System.currentTimeMillis();
            JSONObject jsObj = getRegisterJson();

            try {
                if (jsObj == null) {
                    return false;
                }

                ScoutingDBHelper.getInstance().setId(jsObj.getInt("id"));

                JSONArray matches = jsObj.getJSONArray("matches");

                for (int i = 0; i < matches.length(); i ++) {
                    SyncHelper.addMatchFromJson(matches.getJSONObject(i));
                }

                ScoutingDBHelper.getInstance().sortMatches();

                JSONArray teams = jsObj.getJSONArray("teams");
                JSONArray conflicts = jsObj.getJSONArray("conflicts");

                for (int i = 0; i < teams.length(); i ++) {
                    boolean conflicted = false;

                    JSONArray team = teams.getJSONArray(i);
                    int num = team.getInt(0);
                    for (int j = 0; j < conflicts.length(); j ++) {
                        if (conflicts.getInt(j) == num) {
                            conflicted = true;
                            break;
                        }
                    }
                    SyncHelper.addTeamFromJson(teams.getJSONArray(i), conflicted);

                }

                ScoutingDBHelper.getInstance().sortTeams();

                JSONArray questions = jsObj.getJSONArray("questions");

                for (int i = 0; i < questions.length(); i ++) {
                    SyncHelper.addQuestionFromJson(questions.getJSONObject(i));
                }

                ScoutingDBHelper.getInstance().sortQuestions();

                long dt = System.currentTimeMillis() - t0;

                Log.d("SYNC", "Time to sync: " + dt);

                return true;
            } catch (JSONException e) {
                Log.e(DownloadDataTask.class.getName(), "Malformed registration.");
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if (result == null) {
                return;
            }
            else if (result) {
                invalidateData();
            }
            else {
                Toast.makeText(MainActivity.this, "Could not connect to scouting server. Check settings.", Toast.LENGTH_LONG).show();
            }

            execTask = false;
        }
    }

    private class UploadDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            if (execTask) {
                return null;
            }

            execTask = true;

            long start = System.currentTimeMillis();

            JSONObject toSend = new JSONObject();

            JSONObject matchQuestionAnswers = new JSONObject();

            List<Question<Match>> matchQuestions = new ArrayList<Question<Match>>();

            for (MatchQuestion mq : ScoutingDBHelper.getInstance().getMatchQuestions()) {
                matchQuestions.add(mq);
            }

            for (Match m : ScoutingDBHelper.getInstance().getMatches()) {
                try {
                    matchQuestionAnswers.put(Integer.toString(m.getNumber()),
                            SyncHelper.getAnswersAsJSON(matchQuestions, m));
                } catch (JSONException e) {
                    continue;
                }
            }

            try {
                toSend.put("match_ans", matchQuestionAnswers);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

            List<Question<Team>> teamQuestions = new ArrayList<Question<Team>>();

            JSONObject teamQuestionAnswers = new JSONObject();

            for (TeamQuestion t : ScoutingDBHelper.getInstance().getTeamQuestions()) {
                teamQuestions.add(t);
            }

            for (Team t : ScoutingDBHelper.getInstance().getTeams()) {
                try {
                    teamQuestionAnswers.put(Integer.toString(t.getNumber()),
                            SyncHelper.getAnswersAsJSON(teamQuestions, t));
                } catch (JSONException e) {
                    continue;
                }
            }

            try {
                toSend.put("team_ans", teamQuestionAnswers);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

            String baseaddr = prefs.getString("server_addr", "failure");

            HttpClient client = new DefaultHttpClient();

            HttpPost post = new HttpPost("http://" + baseaddr + ":8000/api/upload");

            Log.d("MATCHSYNC", post.getURI().toString());

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("data", toSend.toString()));

            try {
                post.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = client.execute(post);

                StatusLine sl = response.getStatusLine();
                int statusCode = sl.getStatusCode();

                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader br = new BufferedReader(new InputStreamReader(content));

                    StringBuilder textSb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        textSb.append(line);
                    }

                    String text = textSb.toString();

                    if (text.toLowerCase().contains("error")) {
                        Log.e("SYNCBACK", "Error from server: '" + text + "'");
                        return false;
                    }
                }
                else {
                    Log.e("SYNCBACK", "Failed response code: " + statusCode);
                    return false;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e("SYNCBACK", "Unsupported URL encoding.");
                return false;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.e("SYNCBACK", "Client protocol failure.");
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("SYNCBACK", "Generic IO exception.");
                return false;
            }

            Log.d("Sync", "Send result: " + toSend.toString());
            Log.d("Sync", "Time to process: " + (System.currentTimeMillis() - start));

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result == null) {
                return;
            }
            else if (result) {
                Toast.makeText(MainActivity.this, "Data sync successful!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(MainActivity.this, "An error occurred while syncing to the scouting " +
                        "server. Please check your settings.", Toast.LENGTH_LONG).show();
            }

            execTask = false;
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
