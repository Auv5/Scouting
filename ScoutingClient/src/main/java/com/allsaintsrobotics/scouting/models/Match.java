package com.allsaintsrobotics.scouting.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jack on 11/26/13.
 */
public class Match {
    public enum Alliance {
        BLUE,
        RED
    }

    int number;

    private Map<Team, TeamScore> blue;

    private Map<Team, TeamScore> red;

    Team[] redO, blueO;

    private class TeamScore {
        public TeamScore(Alliance alliance, int auto, int teleop, int special) {
            this.alliance = alliance;

            this.auto = auto;
            this.teleop = teleop;
            this.special = special;
        }

        Alliance alliance;

        int auto, teleop, special;
    }

    public Match(int number, Team[] blue, Team[] red, int[] blueAuto, int[] redAuto, int[] blueTeleop,
                 int[] redTeleop, int[] blueSpecial, int[] redSpecial) {
        if (blue == null || red == null || blueAuto == null || redAuto == null ||
                blueTeleop == null || redTeleop == null || blueSpecial == null || redSpecial == null) {
            throw new IllegalStateException("None of the passed arrays can be null.");
        }
        if (blue.length != 3 || red.length != 3 || blueAuto.length != 3 || redAuto.length != 3 ||
                blueTeleop.length != 3 || redTeleop.length != 3 || blueSpecial.length != 3 ||
                redSpecial.length != 3) {
            throw new IllegalStateException("All arrays must be length 3.");
        }

        this.number = number;

        this.red = new HashMap<Team, TeamScore>();
        this.blue = new HashMap<Team, TeamScore>();

        this.redO = red;
        this.blueO = blue;

        for (int i = 0; i < 3; i++) {
            int auto = blueAuto[i];
            int teleop = blueTeleop[i];
            int special = blueSpecial[i];

            this.blue.put(blue[i], new TeamScore(Alliance.BLUE, auto, teleop, special));
        }

        for (int i = 0; i < 3; i++) {
            int auto = redAuto[i];
            int teleop = redTeleop[i];
            int special = redSpecial[i];

            this.red.put(red[i], new TeamScore(Alliance.RED, auto, teleop, special));
        }
    }

    public Alliance getAlliance(Team t) {
        if (red.containsKey(t)) {
            return Alliance.RED;
        }
        else if (blue.containsKey(t)) {
            return Alliance.BLUE;
        }

        return null;
    }

    public Team getTeam(Alliance a, int i) {
        if (a == Alliance.RED) {
            return redO[i];
        }
        else if (a == Alliance.BLUE) {
            return blueO[i];
        }

        return null;
    }

    public boolean isPlaying(Team t) {
        if (getAlliance(t) == null) {
            return false;
        }

        return true;
    }

    private Map<Team, TeamScore> getMap(Team t) {
        if (blue.containsKey(t)) {
            return blue;
        }
        else if (red.containsKey(t)) {
            return red;
        }

        return null;
    }

    public int getNumber() {
        return number;
    }

    public int getAuto(Team t) {
        Map<Team, TeamScore> m = getMap(t);

        if (m == null) {
            return -1;
        }

        return m.get(t).auto;
    }

    public int getTeleop(Team t) {
        Map<Team, TeamScore> m = getMap(t);

        if (m == null) {
            return -1;
        }

        return m.get(t).teleop;
    }

    public int getSpecial(Team t) {
        Map<Team, TeamScore> m = getMap(t);

        if (m == null) {
            return -1;
        }

        return m.get(t).special;
    }

    public void setAuto(Team t, int value) {
        Map<Team, TeamScore> m = getMap(t);

        if (m == null) {
            throw new IllegalStateException("That team isn't playing!");
        }

        TeamScore ts = m.get(t);

        if (ts.auto != -1) {
            throw new IllegalStateException("Cannot set auto match score again...");
        }

        ts.auto = value;
    }

    public void setTeleop(Team t, int value) {
        Map<Team, TeamScore> m = getMap(t);

        if (m == null) {
            throw new IllegalStateException("That team isn't playing!");
        }

        TeamScore ts = m.get(t);

        if (ts.teleop != -1) {
            throw new IllegalStateException("Cannot set teleop match score again...");
        }

        ts.teleop = value;
    }

    public void setSpecial(Team t, int value) {
        Map<Team, TeamScore> m = getMap(t);

        if (m == null) {
            throw new IllegalStateException("That team isn't playing!");
        }

        TeamScore ts = m.get(t);

        if (ts.special != -1) {
            throw new IllegalStateException("Cannot set teleop match score again...");
        }

        ts.special = value;
    }

    public boolean hasTeam(Team t) {
        for (int i = 0; i < 3; i ++) {
            if (redO[i].equals(t) || blueO[i].equals(t)) {
                return true;
            }
        }

        return false;
    }
}
