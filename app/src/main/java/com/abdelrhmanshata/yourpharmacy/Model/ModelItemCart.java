package com.abdelrhmanshata.yourpharmacy.Model;

import java.io.Serializable;

public class ModelItemCart implements Serializable {
    String ID,
            medicineName;
    int numOfMedicine;
    double totalPrice;

    public ModelItemCart() {
    }

    public ModelItemCart(String ID, String medicineName, int numOfMedicine, double totalPrice) {
        this.ID = ID;
        this.medicineName = medicineName;
        this.numOfMedicine = numOfMedicine;
        this.totalPrice = totalPrice;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getNumOfMedicine() {
        return numOfMedicine;
    }

    public void setNumOfMedicine(int numOfMedicine) {
        this.numOfMedicine = numOfMedicine;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
