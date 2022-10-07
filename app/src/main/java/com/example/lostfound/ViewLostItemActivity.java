package com.example.lostfound;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewLostItemActivity extends AppCompatActivity {

    //variables for viewing item
    LinearLayout viewItemLayout;
    TextView itemTypeTextView, imageUriStrTextView, timestampTextView, contactInfoTextView, claimItemTextView;
    ImageView imageView;
    Bitmap bitmap;

    //variables for claiming item
    LinearLayout claimItemLayout;
    TextView cancelClaimTextView, confirmClaimTextView;
    EditText matrixNumberEditText, nameEditText, tutorialEditText, emailEditText, itemDetailEditText, estimatePriceEditText;
    Timestamp timestampClaimed;

    //general variables
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;
    private String itemType, imageUriStr,localFilePath, timestampReported, contactInfo, docID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lost_item);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //defining viewing item variables
        viewItemLayout = findViewById(R.id.view_lost_item_layout);
        itemTypeTextView = findViewById(R.id.view_lost_item_type_text_view);
        imageUriStrTextView = findViewById(R.id.view_lost_image_uri_string);
        timestampTextView = findViewById(R.id.view_lost_item_timestamp_text_view);
        contactInfoTextView = findViewById(R.id.view_lost_contact_info_text_view);
        claimItemTextView = findViewById(R.id.claim_lost_item_text_view);
        imageView =  findViewById(R.id.view_lost_item_image_view);

        //defining claim item variables
        claimItemLayout = findViewById(R.id.claim_lost_item_layout);
        cancelClaimTextView = findViewById(R.id.cancel_claim_lost_item_text_view);
        confirmClaimTextView = findViewById(R.id.confirm_claim_lost_item_text_view);
        matrixNumberEditText = findViewById(R.id.claim_matrix_number_edit_text);
        nameEditText = findViewById(R.id.claim_name_edit_text);
        tutorialEditText = findViewById(R.id.claim_tutorial_edit_text);
        emailEditText = findViewById(R.id.claim_email_edit_text);
        itemDetailEditText = findViewById(R.id.claim_item_description_edit_text);
        estimatePriceEditText = findViewById(R.id.claim_item_price_edit_text);
        timestampClaimed = Timestamp.now();

        //display viewing info
        itemType = getIntent().getStringExtra("itemType");
        imageUriStr = getIntent().getStringExtra("imageUriStr");
        localFilePath = getIntent().getStringExtra("localFilePath");
        timestampReported = getIntent().getStringExtra("timestampReported");
        contactInfo = getIntent().getStringExtra("contactInfo");
        docID = getIntent().getStringExtra("docID");

        bitmap = BitmapFactory.decodeFile(localFilePath);

        itemTypeTextView.setText(itemType);
        imageUriStrTextView.setText(imageUriStr);
        imageView.setImageBitmap(bitmap);
        timestampTextView.setText(timestampReported);
        contactInfoTextView.setText(contactInfo);

        //functions and buttons
        claimItemTextView.setOnClickListener(v-> {
            viewItemLayout.setVisibility(viewItemLayout.GONE);
            claimItemLayout.setVisibility(claimItemLayout.VISIBLE);
        });
        cancelClaimTextView.setOnClickListener(v-> {
            viewItemLayout.setVisibility(viewItemLayout.VISIBLE);
            claimItemLayout.setVisibility(claimItemLayout.GONE);
        });

        confirmClaimTextView.setOnClickListener(v-> {
            DocumentReference reporterDataDocRef = Utility.getDocumentReferenceUserData(currentUser);
            reporterDataDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot unclaimedDocumentSnapshot, @Nullable FirebaseFirestoreException error) {
                    //update details of claimer
                    String nameOfClaimer = unclaimedDocumentSnapshot.getString("name");
                    String matrixNoOfClaimer = unclaimedDocumentSnapshot.getString("matrixNo");
                    DocumentReference unclaimedDocRef = Utility.getCollectionReferenceUnclaimed(itemType).document(docID);
                    unclaimedDocRef.update("nameOfClaimer", nameOfClaimer);
                    unclaimedDocRef.update("matrixNoOfClaimer", matrixNoOfClaimer);
                    unclaimedDocRef.update("timestampClaimed", timestampClaimed);
                    unclaimedDocRef.update("estimatePrice", estimatePriceEditText.getText().toString());
                    Utility.showToast(ViewLostItemActivity.this, "Successfully claimed");
                }
            });

            //move data of item to claimed collection
            Utility.getCollectionReferenceClaimed(itemType).document(docID);
            DocumentReference claimedDocRef = Utility.getCollectionReferenceClaimed(itemType).document(docID);
            DocumentReference unclaimedDocRef = Utility.getCollectionReferenceUnclaimed(itemType).document(docID);
            unclaimedDocRef.addSnapshotListener(ViewLostItemActivity.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot claimedDocumentSnapshot, @Nullable FirebaseFirestoreException error) {
                    LostItem claimedItemDetails = new LostItem();
                    claimedItemDetails.setContactInfo(claimedDocumentSnapshot.getString("contactInfo"));
                    claimedItemDetails.setEstimatePrice(claimedDocumentSnapshot.getString("estimatePrice"));
                    claimedItemDetails.setImageUriStr(claimedDocumentSnapshot.getString("imageUriStr"));
                    claimedItemDetails.setItemType(claimedDocumentSnapshot.getString("itemType"));
                    claimedItemDetails.setMatrixNoOfClaimer(claimedDocumentSnapshot.getString("matrixNoOfClaimer"));
                    claimedItemDetails.setMatrixNoOfReporter(claimedDocumentSnapshot.getString("matrixNoOfReporter"));
                    claimedItemDetails.setNameOfClaimer(claimedDocumentSnapshot.getString("nameOfClaimer"));
                    claimedItemDetails.setNameOfReporter(claimedDocumentSnapshot.getString("nameOfReporter"));
                    claimedItemDetails.setTimestampClaimed(claimedDocumentSnapshot.getTimestamp("timestampClaimed"));
                    claimedItemDetails.setTimestampReported(claimedDocumentSnapshot.getTimestamp("timestampClaimed"));
                    claimedDocRef.set(claimedItemDetails);
                }
            });
            finish();
        });
    }
}