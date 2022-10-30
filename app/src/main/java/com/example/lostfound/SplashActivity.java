package com.example.lostfound;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser==null){
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }else{
                    if(currentUser.isEmailVerified()){
                        GlobalVariables.currentUser = currentUser;
                        GlobalVariables.currentUserID = GlobalVariables.currentUser.getUid();
                        GlobalVariables.userDataDocRef = Utility.getDocumentReferenceUserData(GlobalVariables.currentUser);
                        GlobalVariables.userDataDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot userData, @Nullable FirebaseFirestoreException error) {
                                GlobalVariables.name = userData.getString("name");
                                GlobalVariables.matrixNo = userData.getString("matrixNo");
                                GlobalVariables.key = userData.getString("key");
                                GlobalVariables.organization = userData.getString("organization");
                            }
                        });
                        GlobalVariables.firebaseStorage = FirebaseStorage.getInstance();
                        GlobalVariables.storageReference = GlobalVariables.firebaseStorage.getReference();

                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }else{
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                }
                finish();
            }
        }, 750);
    }
}