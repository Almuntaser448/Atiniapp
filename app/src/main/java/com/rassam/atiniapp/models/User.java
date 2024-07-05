package com.rassam.atiniapp.models;

//import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
//import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;
//import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User {
    private String userId;
    private int watermelon;
    private String username;
    private String email;
    private String ProfilePhotoUrl;
    private List<String> favorites =new ArrayList<>();
    private List<String> chatPartners =new ArrayList<>();
    private Rating rating;
    private List<Notification> notifications;
    private static final int MAX_NOTIFICATIONS = 10;
    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
        this.watermelon=20;
    }
    public User(String userId, String username,String email) {
        this.userId = userId;
        this.username = username;
        this.email=email;
        this.watermelon=20;


    }
    public User() {
    }
    public User(String userId, String username, List<String> favorites) {
        this.userId = userId;
        this.username = username;
        this.favorites = favorites;
        this.watermelon=20;

    }


    public int getWatermelon() {
        return watermelon;
    }

    public void setWatermelon(int watermelom) {
        this.watermelon = watermelom;
    }

    public void setChatPartners(List<String> chatPartners) {
        this.chatPartners = chatPartners;
    }

    @Exclude
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public List<String> getFavorites() {
        return favorites;
    }


    public String getProfilePhotoUrl() {
        return ProfilePhotoUrl;
    }

    public void setProfilePhotoUrl(String ProfilePhotoUrl) {
        this.ProfilePhotoUrl = ProfilePhotoUrl;
    }
    public List<Notification> getNotifications() {
        if (this.notifications == null) {
            this.notifications = new ArrayList<>(MAX_NOTIFICATIONS); // Initialize if null
        }
        return this.notifications;
    }

    public List<String> getChatPartners() {
        return chatPartners;
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

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }
}
