package com.example.lostfound;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {

    ImageButton backButton;
    TextView nameTextView, matrixNoTextView, creditsTextView;

    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        backButton = findViewById(R.id.profile_back_button);
        nameTextView = findViewById(R.id.profile_user_name_text_view);
        matrixNoTextView = findViewById(R.id.profile_matrix_no_text_view);
        creditsTextView = findViewById(R.id.profile_credits_text_view);

        DocumentReference userDateDocRef = Utility.getDocumentReferenceUserData(currentUser);
        userDateDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot userData, @Nullable FirebaseFirestoreException error) {
                nameTextView.setText(userData.getString("name"));
                matrixNoTextView.setText(userData.getString("matrixNo"));
                creditsTextView.setText((userData.getLong("credits")).toString());
            }
        });

        backButton.setOnClickListener(v-> {
            finish();
        });
    }
}