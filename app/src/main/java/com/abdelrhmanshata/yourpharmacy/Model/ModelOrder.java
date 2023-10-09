package com.abdelrhmanshata.yourpharmacy.Model;

import java.io.Serializable;

public class ModelOrder implements Serializable {

    String ID, userID, userName, userPhone;
    String userAddress, Latitude, Longitude;
    String date , time, typePayment;
    boolean InProcess,InRoad,Delivered;

    public ModelOrder() {
    }

    public ModelOrder(String ID, String userID, String userName, String userPhone, String userAddress, String latitude, String longitude, String date, String time, String typePayment, boolean inProcess, boolean inRoad, boolean delivered) {
        this.ID = ID;
        this.userID = userID;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        Latitude = latitude;
        Longitude = longitude;
        this.date = date;
        this.time = time;
        this.typePayment = typePayment;
        InProcess = inProcess;
        InRoad = inRoad;
        Delivered = delivered;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
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

    public String getTypePayment() {
        return typePayment;
    }

    public void setTypePayment(String typePayment) {
        this.typePayment = typePayment;
    }

    public boolean isInProcess() {
        return InProcess;
    }

    public void setInProcess(boolean inProcess) {
        InProcess = inProcess;
    }

    public boolean isInRoad() {
        return InRoad;
    }

    public void setInRoad(boolean inRoad) {
        InRoad = inRoad;
    }

    public boolean isDelivered() {
        return Delivered;
    }

    public void setDelivered(boolean delivered) {
        Delivered = delivered;
    }
}
