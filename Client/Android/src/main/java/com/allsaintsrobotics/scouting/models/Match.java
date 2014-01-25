package com.allsaintsrobotics.scouting.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jack on 11/26/13.
 */
public class Match implements Parcelable {
    int number;

    private int[] red, blue;

    private int scout;

    public enum Alliance {
        BLUE,
        RED
    }

    public Match(int number, int[] blue, int[] red, int scout) {
        this.number = number;

        this.red = red;
        this.blue = blue;

        this.scout = scout;
    }

    public Match(Parcel in) {
        this(in.readInt(), in.createIntArray(), in.createIntArray(), in.readInt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
        dest.writeIntArray(red);
        dest.writeIntArray(blue);
        dest.writeInt(scout);
    }

    public static final Creator<Match> CREATOR = new Creator<Match>() {
        @Override
        public Match createFromParcel(Parcel source) {
            return new Match(source);
        }

        @Override
        public Match[] newArray(int size) {
            return new Match[size];
        }
    };

    @Override
    public int hashCode() {
        return 142 * number;
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

    public int getScout() {
        return scout;
    }
}
