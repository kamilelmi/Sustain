package com.sustain.model;

/**
 * @author Ghulam Ali
 * Seamless Distribution Systems™
 * Author Email: ghulam.ali@seamless.se
 * Created on: 02/05/2023
 */
public class Follower
{
    public String userName;

    public String uid;


    public int invitationCount;

    public Boolean isFollow;

    public Boolean isInvited = false;


    public Follower()
    {

    }

    public Follower(String userName, String uid, Boolean isFollow,int invitationCount)
    {
        this.uid = uid;
        this.userName = userName;
        this.isFollow = isFollow;
        this.invitationCount=invitationCount;
    }

    public Boolean getInvited()
    {
        return isInvited;
    }

    public void setInvited(Boolean isInvited)
    {
        this.isInvited = isInvited;
    }
}
