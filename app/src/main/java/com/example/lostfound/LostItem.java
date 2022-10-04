package com.example.lostfound;

import com.google.firebase.Timestamp;

public class LostItem {
    String nameOfReporter;
    String matrixNoOfReporter;
    String nameOfClaimer;
    String matrixNoOfClaimer;
    String itemType;
    String contactInfo;
    String imageUriStr;
    Timestamp timestamp;

    public LostItem() {
    }

    public String getNameOfReporter() {
        return nameOfReporter;
    }

    public void setNameOfReporter(String nameOfReporter) {
        this.nameOfReporter = nameOfReporter;
    }

    public String getMatrixNoOfReporter() {
        return matrixNoOfReporter;
    }

    public void setMatrixNoOfReporter(String matrixNoOfReporter) {
        this.matrixNoOfReporter = matrixNoOfReporter;
    }

    public String getNameOfClaimer() {
        return nameOfClaimer;
    }

    public void setNameOfClaimer(String nameOfClaimer) {
        this.nameOfClaimer = nameOfClaimer;
    }

    public String getMatrixNoOfClaimer() {
        return matrixNoOfClaimer;
    }

    public void setMatrixNoOfClaimer(String matrixNoOfClaimer) {
        this.matrixNoOfClaimer = matrixNoOfClaimer;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getImageUriStr() {
        return imageUriStr;
    }

    public void setImageUriStr(String imageUriStr) {
        this.imageUriStr = imageUriStr;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
