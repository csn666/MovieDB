package com.example.csanz.moviedb.Data;

import java.io.Serializable;

public class Movie implements Serializable {
    private String title;
    private String date;
    private String overview;

    //Get title
    public String getTitle() {
        return title;
    }

    //Set title
    public void setTitle(String title) {
        this.title = title;
    }

    //Get date
    public String getDate() {
        return date;
    }

    //Set date
    public void setDate(String date) {
        this.date = date;
    }

    //Get overview
    public String getOverview() {
        return overview;
    }

    //Set overview
    public void setOverview(String overview) {
        this.overview = overview;
    }
}
