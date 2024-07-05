package com.rassam.atiniapp.models;

import java.util.Date;

public class Notification {
    private String message;
    private boolean isRead;

    private Date timestamp;
    public Notification() {}
    public Notification(String message, boolean isRead, Date timestamp) {
        this.message = message;
        this.isRead = isRead;

        this.timestamp = timestamp;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public void setRead(boolean read) {
        isRead = read;
    }




    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isRead;
    }


    public Date getTimestamp() {
        return timestamp;
    }
}