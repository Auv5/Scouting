package com.allsaintsrobotics.scouting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jack on 7/31/13.
 */
public class Team implements Parcelable {
    private int number;
    private String name;

//    private List<Match> matches = null;

    public Team(int number, String name)
    {
        this.number = number;
        this.name = name;
    }

    private Team(Parcel p)
    {
        number = p.readInt();
        name = p.readString();
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Team> CREATOR = new Parcelable.Creator<Team>() {
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(number);
        out.writeString(name);
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof Team && this.getNumber() == ((Team) o).getNumber();
    }

    @Override
    public int hashCode()
    {
        // Use the team number as a hashcode.
        return getNumber();
    }
}
