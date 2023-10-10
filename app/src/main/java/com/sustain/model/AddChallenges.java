package com.sustain.model;

/**
 * @author Ghulam Ali
 * Seamless Distribution Systems™
 * Author Email: ghulam.ali@seamless.se
 * Created on: 04/05/2023
 */
public class AddChallenges
{
    public String title;
    public String description;
    public String endTime;
    public String startTime;
    public String createdBy;

    public String category;

    public AddChallenges()
    {
    }

    public AddChallenges(String title, String description, String startTime, String endTime, String createdBy, String category)
    {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdBy = createdBy;
        this.category = category;
    }
}
