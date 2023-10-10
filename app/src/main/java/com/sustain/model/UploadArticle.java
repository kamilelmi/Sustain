package com.sustain.model;

/**
 * @author Ghulam Ali
 * Seamless Distribution Systems™
 * Author Email: ghulam.ali@seamless.se
 * Created on: 02/05/2023
 */
public class UploadArticle
{
    public String articleUri;

    public String thumbnailUri;

    public String articleTitle;

    public UploadArticle()
    {

    }

    public UploadArticle(String articleTitle, String articleUri, String thumbnailUri)
    {
        this.articleTitle = articleTitle;
        this.articleUri = articleUri;
        this.thumbnailUri = thumbnailUri;
    }
}
