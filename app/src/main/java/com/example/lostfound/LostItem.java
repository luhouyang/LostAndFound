package com.example.lostfound;

import com.google.firebase.Timestamp;

public class LostItem {
    String itemType;
    String contactInfo;
    String imageUriStr;
    Timestamp timestamp;

    public LostItem() {
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
