package com.example.lostfound;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class Utility {
    static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    static CollectionReference getCollectionReferenceUnclaimed(String itemType){
        return FirebaseFirestore
                .getInstance()
                .collection("unclaimed-items")
                .document("Unclaimed-Items")
                .collection(itemType);
    }

    static String timeToString(Timestamp timestamp){

        return new SimpleDateFormat("MM/dd/yyyy").format(timestamp.toDate());
    }
}
