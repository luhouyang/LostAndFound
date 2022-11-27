package com.example.lostfound;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Pattern;

public class GlobalVariables extends AppCompatActivity {

    // User ID & Reference
    public static FirebaseUser currentUser;
    public static String currentUserID;
    public static DocumentReference userDataDocRef;

    // Firebase database reference
    public static FirebaseStorage firebaseStorage;
    public static StorageReference storageReference;

    // User info
    public static String name;
    public static String organization;
    public static String identification;
    public static String matrixNo;
    public static String key;
    public static int credits;

    // Patterns
    public static Pattern checkPattern;

    //General
    public static boolean reportedItem;
    public static boolean complainItem;

}
