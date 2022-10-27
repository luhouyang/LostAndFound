package com.example.lostfound;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Pattern;

public class GlobalVariables {

    // User ID & Reference
    public static FirebaseUser currentUser;
    public static DocumentReference userDataDocRef;

    // Firebase database reference
    public static FirebaseStorage firebaseStorage;
    public static StorageReference storageReference;

    // User info
    public static String name;
    public static String organization;
    public static String matrixNo;
    public static String key;

    // Patterns
    public static Pattern checkPattern;
}
