package com.rassam.atiniapp.models;

import java.util.Date;

public class Reservation {
private boolean isReserved;
private String senderUserId;
private String receiverUserId;
private Date dueDate;
    public Reservation() {}
public Reservation(boolean isReserved, String senderUserId,String receiverUserId, Date dueDate) {
    this.isReserved = isReserved;
    this.senderUserId=senderUserId;
    this.receiverUserId = receiverUserId
    ;this.dueDate = dueDate;
}

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public void setReceiverUserId(String receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public String getReceiverUserId() {
        return receiverUserId;
    }

    public Date getDueDate() {
        return dueDate;
    }
// Getters and setters
// ...
}