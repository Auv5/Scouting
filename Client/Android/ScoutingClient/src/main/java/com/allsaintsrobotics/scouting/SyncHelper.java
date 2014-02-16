package com.allsaintsrobotics.scouting;

import com.allsaintsrobotics.scouting.models.Match;
import com.allsaintsrobotics.scouting.survey.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by jack on 12/2/13.
 */
public class SyncHelper {
    public static void addMatchFromJson(JSONObject matchJson) throws JSONException {
        int matchId = matchJson.getInt("id");
        int scout = matchJson.getInt("scout");

        JSONArray jsonRed = matchJson.getJSONArray("red");
        JSONArray jsonBlue = matchJson.getJSONArray("blue");

        if (jsonRed.length() != 3 || jsonBlue.length() != 3) {
            throw new JSONException("Red and blue alliances must have three teams.");
        }

        int[] red = new int[3];
        int[] blue = new int[3];

        for (int i = 0; i < jsonRed.length(); i ++)
        {
            red[i] = jsonRed.getInt(i);
            blue[i] = jsonBlue.getInt(i);
        }

        ScoutingDBHelper.getInstance().addMatch(matchId, scout, red, blue);
    }

    public static void addTeamFromJson(JSONArray teamJson) throws JSONException {
        int teamNum = teamJson.getInt(0);
        String teamNickname = teamJson.getString(1);

        ScoutingDBHelper.getInstance().addTeam(teamNum, teamNickname);
    }

    public static void addQuestionFromJson(JSONObject jsonObject) throws JSONException {
        int id = jsonObject.getInt("id");

        String label = jsonObject.getString("label");
        String type = jsonObject.getString("type");
        JSONArray offersJson = jsonObject.getJSONArray("offers");
        String[] offers = new String[offersJson.length()];

        for (int i = 0; i < offers.length; i ++) {
            offers[i] = offersJson.getString(i);
        }

        ScoutingDBHelper.getInstance().
                addQuestion(id, label, type, offers);
    }

    public static <T> JSONArray getAnswersAsJSON(List<Question<T>> questions, T t) {
        JSONArray arr = new JSONArray();

        for (Question<T> q : questions) {
            JSONObject json = q.getJSON(t);
            if (t instanceof Match) {
                try {
                    json.put("scout", ((Match) t).getScout());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            arr.put(json);
        }

        return arr;
    }
}

