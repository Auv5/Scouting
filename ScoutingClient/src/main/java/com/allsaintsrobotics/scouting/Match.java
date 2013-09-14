package com.allsaintsrobotics.scouting;

import java.util.Random;

/**
 * Created by Jack on 7/31/13.
 */

public class Match {
    enum Alliance
    {
        RED,
        BLUE
    }

    class MatchScore
    {

        MatchScore(int teamNum, Alliance alliance, int teleop, int autonomous, int foul) {
            this.teamNum = teamNum;
            this.alliance = alliance;
            this.teleop = teleop;
            this.autonomous = autonomous;
            this.foul = foul;
        }

        MatchScore() {}

        private int teamNum;
        private Alliance alliance;
        private int teleop;
        private int autonomous;
        private int foul;

        public int getTeamNum()
        {
            return teamNum;
        }

        public Alliance getAlliance()
        {
            return alliance;
        }

        public int getTeleop()
        {
            return teleop;
        }

        public int getFoul()
        {
            return foul;
        }

        public int getAutonomous()
        {
            return autonomous;
        }

        void setTeamNum(int teamNum)
        {
            this.teamNum = teamNum;
        }

        void setAlliance(Alliance alliance)
        {
            this.alliance = alliance;
        }

        void setTeleop(int teleop)
        {
            this.teleop = teleop;
        }

        void setAutonomous(int autonomous)
        {
            this.autonomous = autonomous;
        }

        void setFoul(int foul)
        {
            this.foul = foul;
        }
    }

    private int number;

    public final static int TEAMS_PER_ALLIANCE = 3;
    public final static int TEAMS_PER_MATCH = TEAMS_PER_ALLIANCE*2;

    // Red team
    private Team[] red = null;
    // Blue team
    private Team[] blue = null;
    // All teams
    private Team[] teams = null;

    private int[] teleop = new int[TEAMS_PER_MATCH];

    private int[] auto = new int[TEAMS_PER_MATCH];

    // Total red foul points
    private int[] foul = new int[TEAMS_PER_MATCH];

    /* Actual totals. Used to see scouter error (data reliability). */
    // Actual red total score
    private int actualRed = 0;
    // Actual blue total score
    private int actualBlue = 0;

    public Match(int num, Team red1, Team red2, Team red3, Team blue1, Team blue2, Team blue3,
                 int actualRed, int actualBlue)
    {
        this.number = num;
        red = new Team [] {red1, red2, red3};
        blue = new Team [] {blue1, blue2, blue3};
        teams = new Team [] {red1, red2, red3, blue1, blue2, blue3};
        this.actualRed = actualRed;
        this.actualBlue = actualBlue;
    }

    public Match()
    {
        red = new Team[TEAMS_PER_ALLIANCE];
        blue = new Team[TEAMS_PER_ALLIANCE];
        teams = new Team[TEAMS_PER_MATCH];

        // Test case mode

        Random r = new Random();

        red[0] = new Team(2056, "");
        teams[0] = red[0];

        for (int i = 1; i < red.length; i ++)
        {
            red[i] = new Team(r.nextInt(5000), "");
            teams[i] = red[i];
        }

        for (int i = 0; i < blue.length; i ++)
        {
            blue[i] = new Team(r.nextInt(5000), "");
            teams[i+3] = blue[i];
        }
    }

    private void addTeam(Alliance a, Team t)
    {
        Team[] arr = (a == Alliance.RED ? red : blue);

        int i = 0;

        while (i < arr.length && arr[i] != null)
        {
            i++;
        }

        if (i == arr.length)
        {
            arr[i] = t;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    private int findIndexForTeam(Team t)
    {
        for (int i = 0; i < teams.length; i ++)
        {
            if (t.equals(teams[i]))
            {
                return i;
            }
        }
        return -1;
    }

    public Team[] getTeams()
    {
        return teams;
    }

    public Team getTeam(Alliance team, int index)
    {
        if (team == Alliance.RED)
        {
            return red[index-1];
        }
        else if (team == Alliance.BLUE)
        {
            return blue[index-1];
        }

        return null;
    }

    public void setFouls(Team t, int foulN)
    {
        int index = findIndexForTeam(t);

        foul[index] = foulN;
    }

    public void setTeleop(Team t, int teleopN)
    {
        int index = findIndexForTeam(t);

        teleop[index] = teleopN;
    }

    public void setAutonomous(Team t, int autoN)
    {
        int index = findIndexForTeam(t);

        auto[index] = autoN;
    }

    public int getFouls(Team t)
    {
        return foul[findIndexForTeam(t)];
    }

    public int getTeleop(Team t)
    {
        return teleop[findIndexForTeam(t)];
    }

    public int getAuto(Team t)
    {
        return auto[findIndexForTeam(t)];
    }

    public int getRedFouls()
    {
        return foul[0] + foul[1] + foul[2];
    }

    public int getBlueFouls()
    {
        return foul[3] + foul[4] + foul[5];
    }

    public int getTeleopPoints(Team t)
    {
        return teleop[findIndexForTeam(t)];
    }

    public int getAutonomousPoints(Team t)
    {
        return teleop[findIndexForTeam(t)];
    }

    public int getTeamTotal(Team t)
    {
        int index = findIndexForTeam(t);

        return teleop[index] + auto[index];
    }

    /**
     * Gets the effective points total for a team during this match. Calculated as:
     * (teleop + auto) - foul
     */
    public int getEffectiveTeamScore(Team t)
    {
        int index = findIndexForTeam(t);

        return (teleop[index] + auto[index]) - foul[index];
    }

    public int getEffectiveRedTotal()
    {
        return getEffectiveTeamScore(red[0]) + getEffectiveTeamScore(red[1]) + getEffectiveTeamScore(red[2]);
    }

    public int getEffectiveBlueTotal()
    {
        return getEffectiveTeamScore(blue[0]) + getEffectiveTeamScore(blue[1]) + getEffectiveTeamScore(blue[2]);
    }

    public int getBlueScoutedTotal()
    {
        return getTeamTotal(blue[0]) + getTeamTotal(blue[1]) + getTeamTotal(blue[2]) + getRedFouls();
    }

    public int getRedScoutedTotal()
    {
        return getTeamTotal(red[0]) + getTeamTotal(red[1]) + getTeamTotal(red[2]) + getBlueFouls();
    }

    public int getActualBlueScore()
    {
        return actualBlue;
    }

    public int getActualRedScore()
    {
        return actualRed;
    }

    public int getBlueErrorPercentage()
    {
        return (int)Math.abs(((getActualBlueScore() - getEffectiveBlueTotal())/((float)getActualBlueScore()))*100);
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int n)
    {
        number = n;
    }
}
