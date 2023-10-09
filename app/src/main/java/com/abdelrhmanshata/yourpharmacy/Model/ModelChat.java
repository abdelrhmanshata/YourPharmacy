package com.abdelrhmanshata.yourpharmacy.Model;

import java.io.Serializable;

public class ModelChat implements Serializable {

    private String ID;
    private String sender;
    private String receiver;
    private String message;
    private String date;
    private String time;

    public ModelChat() {
    }

    public ModelChat(String ID, String sender, String receiver, String message, String date, String time) {
        this.ID = ID;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
