package com.rassam.atiniapp.models;

public class Rating {
    private String ratedUserId;
    private String ratingUserId;
    private float rating;
    private String comment;
    private long timestamp;

    public Rating() {}

    public Rating(String ratedUserId, String ratingUserId, float rating, String comment, long timestamp) {
        this.ratedUserId = ratedUserId;
        this.ratingUserId = ratingUserId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getRatedUserId() {
        return ratedUserId;
    }

    public void setRatedUserId(String ratedUserId) {
        this.ratedUserId = ratedUserId;
    }

    public String getRatingUserId() {
        return ratingUserId;
    }

    public void setRatingUserId(String ratingUserId) {
        this.ratingUserId = ratingUserId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
