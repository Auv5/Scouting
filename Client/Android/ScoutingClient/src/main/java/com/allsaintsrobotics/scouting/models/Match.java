/*
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.allsaintsrobotics.scouting.models;

import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jack on 11/26/13.
 * This file is a part of the ASTECHZ Scouting app.
 */
public class Match implements Parcelable {
    private final int number;

    private final int[] red;
    private final int[] blue;

    private final int scout;

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

    private Match(Parcel in) {
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
        return 142 * number * scout;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Match) {
            return o.hashCode() == this.hashCode();
        }
        else {
            return super.equals(o);
        }
    }

    Alliance getAlliance(int t) {
        if (Arrays.asList(red).contains(t)) {
            return Alliance.RED;
        }
        else if (Arrays.asList(blue).contains(t)) {
            return Alliance.BLUE;
        }

        return null;
    }

    public boolean isPlaying(int t) {
        return getAlliance(t) != null;
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
