package com.rassam.atiniapp.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;

import java.util.List;

@DynamoDBDocument
public class User {
    private String userId;
    private String username;
    private List<Item> favorites;

    public User() {}

    public User(String userId, String username, List<Item> favorites) {
        this.userId = userId;
        this.username = username;
        this.favorites = favorites;
    }

    @DynamoDBHashKey(attributeName = "UserId")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBAttribute(attributeName = "Username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDBAttribute(attributeName = "Favorites")
    public List<Item> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Item> favorites) {
        this.favorites = favorites;
    }
}
