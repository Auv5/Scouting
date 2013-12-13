package com.allsaintsrobotics.scouting.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.allsaintsrobotics.scouting.ScoutingDBHelper;

/**
 * Created by jack on 11/24/13.
 */
public class Team implements Parcelable {
    private int number;
    private String nickname;

    private static String TAG = "team";

    public Team(int number, String nickname) {
        this.number = number;
        this.nickname = nickname;
    }

    public Team(Parcel in) {
        this(in.readInt(), in.readString());
    }

    public int getNumber() {
        return this.number;
    }

    public String getNickname() {
        return this.nickname;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.v(TAG, "writeToParcel..." + flags);

        dest.writeInt(this.number);
        dest.writeString(this.nickname);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || ((Object)this).getClass() != o.getClass()) {
            return false;
        }

        Team team = (Team) o;
        return number == team.number;

    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + nickname.hashCode();
        return result;
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel source) {
            return new Team(source);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    public static Team fromCursor(Cursor cursor) {
        int number = cursor.getInt(cursor.getColumnIndex(ScoutingDBHelper.TEAM_NUM));
        String name = cursor.getString(cursor.getColumnIndex(ScoutingDBHelper.TEAM_NAME));

        return new Team(number, name);
    }
}
