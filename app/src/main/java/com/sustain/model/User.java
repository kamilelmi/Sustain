package com.sustain.model;

/**
 * @author Ghulam Ali
 * Seamless Distribution Systems™
 * Author Email: ghulam.ali@seamless.se
 * Created on: 26/04/2023
 */
public class User
{
    public String userName;
    public String email;
    public String phoneNumber;

    public int followerCount;

    public int followingCount;

    public int articleCount;

    public int logActionCount;

    public int challengesCount;

    public int invitationCount;

    public boolean isPaid;

    public String key, authKey;

    public String stripe_id;


    public User()
    {

    }

    public User(String userName, String email, String phoneNumber, int followerCount, int followingCount, int articleCount, int logActionCount, int challengesCount, int invitationCount, boolean isPaid)
    {
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.followingCount = followingCount;
        this.followerCount = followerCount;
        this.articleCount = articleCount;
        this.logActionCount = logActionCount;
        this.invitationCount = invitationCount;
        this.challengesCount = challengesCount;
        this.isPaid = isPaid;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getAuthKey()
    {
        return authKey;
    }

    public void setAuthKey(String authKey)
    {
        this.authKey = authKey;
    }

    public String getStripeId()
    {
        return stripe_id;
    }

    public void setStripeId(String stripeId)
    {
        this.stripe_id = stripeId;
    }

    public boolean isPaid()
    {
        return isPaid;
    }

    public void setPaid(boolean paid)
    {
        isPaid = paid;
    }
}
