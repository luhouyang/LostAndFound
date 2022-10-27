package com.example.lostfound;

import com.google.firebase.Timestamp;

public class LostItem {

    //general
    String status;

    //reporting
    String nameOfReporter;
    String matrixNoOfReporter;
    String itemType;
    String contactInfo;
    String imageUriStr;
    String place;
    Timestamp timestampReported;

    //claiming
    String nameOfClaimer;
    String matrixNoOfClaimer;
    String tutorialOfClaimer;
    String estimatePrice;
    Timestamp timestampClaimed;

    //complain
    String nameOfComplain;
    String matrixNoOfComplain;
    String tutorialComplain;
    String complainPrice;
    Timestamp timestampComplained;

    public LostItem() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Timestamp getTimestampReported() {
        return timestampReported;
    }

    public void setTimestampReported(Timestamp timestampReported) {
        this.timestampReported = timestampReported;
    }

    public Timestamp getTimestampClaimed() {
        return timestampClaimed;
    }

    public void setTimestampClaimed(Timestamp timestampClaimed) {
        this.timestampClaimed = timestampClaimed;
    }

    public String getTutorialOfClaimer() {
        return tutorialOfClaimer;
    }

    public void setTutorialOfClaimer(String tutorialOfClaimer) {
        this.tutorialOfClaimer = tutorialOfClaimer;
    }

    public String getEstimatePrice() {
        return estimatePrice;
    }

    public void setEstimatePrice(String estimatePrice) {
        this.estimatePrice = estimatePrice;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getNameOfComplain() {
        return nameOfComplain;
    }

    public void setNameOfComplain(String nameOfComplain) {
        this.nameOfComplain = nameOfComplain;
    }

    public String getMatrixNoOfComplain() {
        return matrixNoOfComplain;
    }

    public void setMatrixNoOfComplain(String matrixNoOfComplain) {
        this.matrixNoOfComplain = matrixNoOfComplain;
    }

    public String getTutorialComplain() {
        return tutorialComplain;
    }

    public void setTutorialComplain(String tutorialComplain) {
        this.tutorialComplain = tutorialComplain;
    }

    public String getComplainPrice() {
        return complainPrice;
    }

    public void setComplainPrice(String complainPrice) {
        this.complainPrice = complainPrice;
    }

    public Timestamp getTimestampComplained() {
        return timestampComplained;
    }

    public void setTimestampComplained(Timestamp timestampComplained) {
        this.timestampComplained = timestampComplained;
    }
}
