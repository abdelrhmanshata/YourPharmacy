package com.abdelrhmanshata.yourpharmacy.Model;

import java.io.Serializable;

public class ModelMedicine implements Serializable {

    String medicineID,
            medicineQR,
            medicineName,
            medicineImage,
            medicineType;

    int numOfMedicine;
    double medicinePrice;
    boolean isDiscount;
    double medicineDiscount;

    public ModelMedicine() {
    }

    public ModelMedicine(String medicineID, String medicineQR, String medicineName, String medicineImage, String medicineType, int numOfMedicine, double medicinePrice, boolean isDiscount, double medicineDiscount) {
        this.medicineID = medicineID;
        this.medicineQR = medicineQR;
        this.medicineName = medicineName;
        this.medicineImage = medicineImage;
        this.medicineType = medicineType;
        this.numOfMedicine = numOfMedicine;
        this.medicinePrice = medicinePrice;
        this.isDiscount = isDiscount;
        this.medicineDiscount = medicineDiscount;
    }

    public String getMedicineID() {
        return medicineID;
    }

    public void setMedicineID(String medicineID) {
        this.medicineID = medicineID;
    }

    public String getMedicineQR() {
        return medicineQR;
    }

    public void setMedicineQR(String medicineQR) {
        this.medicineQR = medicineQR;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineImage() {
        return medicineImage;
    }

    public void setMedicineImage(String medicineImage) {
        this.medicineImage = medicineImage;
    }

    public int getNumOfMedicine() {
        return numOfMedicine;
    }

    public void setNumOfMedicine(int numOfMedicine) {
        this.numOfMedicine = numOfMedicine;
    }

    public double getMedicinePrice() {
        return medicinePrice;
    }

    public void setMedicinePrice(double medicinePrice) {
        this.medicinePrice = medicinePrice;
    }

    public double getMedicineDiscount() {
        return medicineDiscount;
    }

    public void setMedicineDiscount(double medicineDiscount) {
        this.medicineDiscount = medicineDiscount;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public boolean isDiscount() {
        return isDiscount;
    }

    public void setDiscount(boolean discount) {
        isDiscount = discount;
    }
}

