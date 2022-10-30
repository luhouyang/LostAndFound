package com.example.lostfound;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {

    ImageButton backButton;
    TextView nameTextView, matrixNoTextView, creditsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backButton = findViewById(R.id.profile_back_button);
        nameTextView = findViewById(R.id.profile_user_name_text_view);
        matrixNoTextView = findViewById(R.id.profile_matrix_no_text_view);
        creditsTextView = findViewById(R.id.profile_credits_text_view);

        GlobalVariables.userDataDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot userData, @Nullable FirebaseFirestoreException error) {
                nameTextView.setText(GlobalVariables.name);
                matrixNoTextView.setText(GlobalVariables.matrixNo);
                creditsTextView.setText(String.valueOf(userData != null ? userData.getLong("credits") : null));
            }
        });

        backButton.setOnClickListener(v-> finish());
    }
}