package com.sustain.model;

import java.io.Serializable;

/**
 * @author Ghulam Ali
 * Seamless Distribution Systems™
 * Author Email: ghulam.ali@seamless.se
 * Created on: 29/04/2023
 */
public class UploadPostData implements Serializable
{

    public UploadPostData()
    {
    }

    public String category, subCategory, image, description,userName;

    public Boolean displayOnFeed;

    public String likeCount;

    public String commentCount;
    public String key, authKey;


    public UploadPostData(String category, String subCategory, String image, String description, Boolean displayOnFeed, String like, String commentCount,String userName)
    {
        this.category = category;
        this.subCategory = subCategory;
        this.image = image;
        this.description = description;
        this.displayOnFeed = displayOnFeed;
        this.likeCount = like;
        this.commentCount = commentCount;
        this.userName=userName;
    }

    public void setLikeCount(String likeCount)
    {
        this.likeCount = likeCount;
    }

    public String getCommentCount()
    {
        return commentCount;
    }

    public void setCommentCount(String commentCount)
    {
        this.commentCount = commentCount;
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


    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getSubCategory()
    {
        return subCategory;
    }

    public void setSubCategory(String subCategory)
    {
        this.subCategory = subCategory;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Boolean getDisplayOnFeed()
    {
        return displayOnFeed;
    }

    public void setDisplayOnFeed(Boolean displayOnFeed)
    {
        this.displayOnFeed = displayOnFeed;
    }

    public String getLikeCount()
    {
        return likeCount;
    }

    public void setLike(String like)
    {
        this.likeCount = like;
    }
}
