package com.rassam.atiniapp.models;

import java.util.List;

public class Item {

        private String id;
        private String title;
        private String category;
        private String description;
        private List<String> photoUrls;
        private String status;
        private int categoryNumber;
        private List<String> keywords;
        private String location;
        private String donorId;
        public Item() {}

        public Item(String id, String title, String category, String description, List<String> photoUrls, String status, int categoryNumber, List<String> keywords, String location) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.description = description;
            this.photoUrls = photoUrls;
            this.status = status;
            this.categoryNumber = categoryNumber;
            this.keywords = keywords;
            this.location = location;
        }


        // New constructor for initializing without itemId
    public Item(String title, String category, String description, String donorId) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.donorId = donorId;
    }

    public String getItemId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Item other = (Item) obj;
        return id.equals(other.id);
    }

    public void setItemId(String itemId) {
        this.id = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public String getDonorId() {
        return donorId;
    }

    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }

    public String getId() {
        return id;
    }

    public int getCategoryNumber() {
        return categoryNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCategoryNumber(int categoryNumber) {
        this.categoryNumber = categoryNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public String getLocation() {
        return location;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
