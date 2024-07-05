package com.rassam.atiniapp.models;

import java.util.List;

public class Item {
    private String id;
    private String userId;
    private String userName;
    private String title;
    private  List<String> keywords;
    private String description;
    private String location;
    private String category; // New field
    private String status;   // New field
    private List<String> photoUrls;
    private boolean isFavorite;
    private Reservation reservation;
    // Default constructor required for calls to DataSnapshot.getValue(Item.class)
    public Item() {
    }

    // Constructor to initialize all fields
    public Item(String id, String userId,String userName, String title, String description, String location, String category, String status, List<String> photoUrls, boolean isFavorite,List<String> keywords) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.title = title;
        this.description = description;
        this.location = location;
        this.category = category;
        this.status = status;
        this.photoUrls = photoUrls;
        this.isFavorite = isFavorite;
        this.keywords=keywords;
        this.reservation=new Reservation(false,userId,null,null);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // Getters and setters for all fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public Reservation getReservation() { return this.reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
    }
}
