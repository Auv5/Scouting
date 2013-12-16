package com.allsaintsrobotics.scouting.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jack on 11/26/13.
 */
public class Match {
    // Three types of standard scores.
    private int auto, teleop, special;

    private String comments;
    private Map<String, String> fields;

    public enum Alliance {
        BLUE,
        RED
    }

    int number;

    private int[] red, blue;

    private int scout;

    public Match(int number, int[] blue, int[] red, int scout, int auto, int teleop, int special,
                 String comments) {
        this.number = number;

        this.red = red;
        this.blue = blue;

        this.auto = auto;
        this.teleop = teleop;
        this.special = special;

        this.comments = comments;
        this.fields = new HashMap<String, String>();

        this.scout = scout;
    }

    public Match(int id, int[] blue, int[] red, int scout) {
        this(id, blue, red, scout, -1, -1, -1, null);
    }

    public Alliance getAlliance(int t) {
        if (Arrays.asList(red).contains(t)) {
            return Alliance.RED;
        }
        else if (Arrays.asList(blue).contains(t)) {
            return Alliance.BLUE;
        }

        return null;
    }

    public boolean isPlaying(int t) {
        if (getAlliance(t) == null) {
            return false;
        }

        return true;
    }

    public int getTeam(Alliance alliance, int i) {
        if (alliance == Alliance.RED) {
            return red[i];
        }

        if (alliance == Alliance.BLUE) {
            return blue[i];
        }

        return -1;
    }

    public int getNumber() {
        return number;
    }

    public int getAuto() {
        return auto;
    }

    public void setAuto(int auto) {
        this.auto = auto;
    }

    public int getTeleop() {
        return teleop;
    }

    public void setTeleop(int teleop) {
        this.teleop = teleop;
    }

    public int getSpecial() {
        return special;
    }

    public void setSpecial(int special) {
        this.special = special;
    }

    public int getScout() {
        return scout;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
