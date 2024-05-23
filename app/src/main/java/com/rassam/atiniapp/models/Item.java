package com.rassam.atiniapp.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;

import java.util.List;

@DynamoDBDocument
public class Item {
    private String itemId;
    private String title;
    private String category;
    private String description;
    private List<String> photoUrls;

    public Item() {}

    public Item(String itemId, String title, String category, String description, List<String> photoUrls) {
        this.itemId = itemId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.photoUrls = photoUrls;
    }

    @DynamoDBAttribute(attributeName = "ItemId")
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @DynamoDBAttribute(attributeName = "Title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDBAttribute(attributeName = "Category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @DynamoDBAttribute(attributeName = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute(attributeName = "PhotoUrls")
    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }
}
