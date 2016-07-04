package com.example.android.popularmovies;

/**
 * Created by firej on 6/29/2016.
 */
public class Movies {

    private String mId, mPosterUrl, mRatings, mOverview, mDate, mTitle;

    public Movies () {

    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        mPosterUrl = posterUrl;
    }

    public String getRatings() {
        return mRatings;
    }

    public void setRatings(String ratings) {
        mRatings = ratings;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
