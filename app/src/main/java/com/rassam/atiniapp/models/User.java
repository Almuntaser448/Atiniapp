package com.rassam.atiniapp.models;

//import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
//import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;
//import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User {
    private String userId;
    private String username;
    private String name;
    private String email;
    private String ProfilePhotoUrl;
    private List<Item> favorites =new ArrayList<>();
    private Rating rating;

    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }
    public User(String userId, String username,String email) {
        this.userId = userId;
        this.username = username;
        this.email=email;
    }
    public User() {
    }
    public User(String userId, String username, List<Item> favorites) {
        this.userId = userId;
        this.username = username;
        this.favorites = favorites;
    }

     @Exclude
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Item> getFavorites() {
        return favorites;
    }

    public String getName() {
        return name;
    }

    public String getProfilePhotoUrl() {
        return ProfilePhotoUrl;
    }

    public void setProfilePhotoUrl(String ProfilePhotoUrl) {
        this.ProfilePhotoUrl = ProfilePhotoUrl;
    }

    public String getEmail() {
        return email;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public void setFavorites(List<Item> favorites) {
        this.favorites = favorites;
    }
}
