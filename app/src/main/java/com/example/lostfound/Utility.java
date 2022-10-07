package com.example.lostfound;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;
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

    static CollectionReference getCollectionReferenceClaimed(String itemType){
        return FirebaseFirestore
                .getInstance()
                .collection("claimed-items")
                .document("Claimed-Items")
                .collection(itemType);
    }

    static DocumentReference getDocumentReferenceUserData(FirebaseUser currentUser){
        return FirebaseFirestore.getInstance().collection("user-data").document(currentUser.getUid());
    }

    static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    static String timeToString(Timestamp timestamp){

        return new SimpleDateFormat("MM/dd/yyyy").format(timestamp.toDate());
    }

    static void debug(Context context){
        showToast(context, "Successful");
    }
}
