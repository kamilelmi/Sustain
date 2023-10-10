package com.sustain.model;

import java.io.Serializable;

/**
 * @author Ghulam Ali
 * Seamless Distribution Systems™
 * Author Email: ghulam.ali@seamless.se
 * Created on: 05/05/2023
 */
public class Invitation implements Serializable
{
    public Boolean isAccepted;

    public AddChallenges challengeData;

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String key;

    public Invitation()
    {

    }

    public Invitation(Boolean isAccepted, AddChallenges challengeData)
    {
        this.isAccepted = isAccepted;
        this.challengeData = challengeData;
    }
}
