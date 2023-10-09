package com.abdelrhmanshata.yourpharmacy.Model;

import java.io.Serializable;

public class ModelDataUser implements Serializable {

    boolean userAdmin;

    String userID,
            userIDNumber,
            userName,
            userAge,
            userEmail,
            userPassword,
            userPhone,
            userAddress,
            userImageUri,
            userType;

    public ModelDataUser() {
    }

    public ModelDataUser(boolean userAdmin, String userID, String userIDNumber, String userName, String userAge, String userEmail, String userPassword, String userPhone, String userAddress, String userImageUri, String userType) {
        this.userAdmin = userAdmin;
        this.userID = userID;
        this.userIDNumber = userIDNumber;
        this.userName = userName;
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        this.userImageUri = userImageUri;
        this.userType = userType;
    }

    public boolean isUserAdmin() {
        return userAdmin;
    }

    public void setUserAdmin(boolean userAdmin) {
        this.userAdmin = userAdmin;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserIDNumber() {
        return userIDNumber;
    }

    public void setUserIDNumber(String userIDNumber) {
        this.userIDNumber = userIDNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
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

    public String getUserImageUri() {
        return userImageUri;
    }

    public void setUserImageUri(String userImageUri) {
        this.userImageUri = userImageUri;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
