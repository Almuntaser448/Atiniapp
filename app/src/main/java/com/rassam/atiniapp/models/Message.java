package com.rassam.atiniapp.models;

import java.util.Date;

public class Message {
    private String senderId;
    private String receiverId;
    private String text;
    private Date timestamp;

    // Empty constructor required for Firestore
    public Message() {}

    public Message(String senderId,String receiverId,String text, Date timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}