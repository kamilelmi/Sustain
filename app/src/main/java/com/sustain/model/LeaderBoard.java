package com.sustain.model;

/**
 * @author Ghulam Ali
 * Seamless Distribution Systems™
 * Author Email: ghulam.ali@seamless.se
 * Created on: 06/05/2023
 */
public class LeaderBoard implements Comparable<LeaderBoard>
{
    public int actionCount;
    public String userName;

    public LeaderBoard()
    {

    }

    public LeaderBoard(int actionCount, String userName)
    {
        this.actionCount = actionCount;
        this.userName = userName;
    }

    @Override
    public int compareTo(LeaderBoard leaderBoard)
    {
        if (leaderBoard.actionCount < this.actionCount)
        {
            return 1;
        } else if (leaderBoard.actionCount > this.actionCount)
        {
            return -1;
        }
        return 0;
    }

}
