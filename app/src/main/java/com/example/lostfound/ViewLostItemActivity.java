package com.example.lostfound;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class ViewLostItemActivity extends AppCompatActivity {

    //variables for viewing item
    LinearLayout viewItemLayout;
    TextView itemTypeTextView, imageUriStrTextView, timestampTextView, placeTextView, contactInfoTextView, claimItemTextView;
    ImageView imageView;
    Bitmap bitmap;

    //variables for claiming item
    LinearLayout claimItemLayout;
    TextView cancelClaimTextView, confirmClaimTextView;
    EditText matrixNumberEditText, nameEditText, tutorialEditText, emailEditText, itemDetailEditText, estimatePriceEditText;
    Timestamp timestampClaimed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lost_item);

        //defining viewing item variables
        viewItemLayout = findViewById(R.id.view_lost_item_layout);
        itemTypeTextView = findViewById(R.id.view_lost_item_type_text_view);
        imageUriStrTextView = findViewById(R.id.view_lost_image_uri_string);
        timestampTextView = findViewById(R.id.view_lost_item_timestamp_text_view);
        placeTextView = findViewById(R.id.view_lost_item_place_text_view);
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
        String itemType = getIntent().getStringExtra("itemType");
        String imageUriStr = getIntent().getStringExtra("imageUriStr");
        String localFilePath = getIntent().getStringExtra("localFilePath");
        String timestampReported = getIntent().getStringExtra("timestampReported");
        String place = getIntent().getStringExtra("place");
        String contactInfo = getIntent().getStringExtra("contactInfo");
        String docID = getIntent().getStringExtra("docID");

        bitmap = BitmapFactory.decodeFile(localFilePath);

        itemTypeTextView.setText(itemType);
        imageUriStrTextView.setText(imageUriStr);
        imageView.setImageBitmap(bitmap);
        timestampTextView.setText(timestampReported);
        placeTextView.setText(place);
        contactInfoTextView.setText(contactInfo);

        //functions and buttons
        claimItemTextView.setOnClickListener(v-> {
            viewItemLayout.setVisibility(View.GONE);
            claimItemLayout.setVisibility(View.VISIBLE);
        });
        cancelClaimTextView.setOnClickListener(v-> {
            viewItemLayout.setVisibility(View.VISIBLE);
            claimItemLayout.setVisibility(View.GONE);
        });

        confirmClaimTextView.setOnClickListener(claim-> {
            String tutorial = tutorialEditText.getText().toString();
            String price = estimatePriceEditText.getText().toString();
            if (isValid(nameEditText.getText().toString(), tutorial, price)){
                DocumentReference unclaimedDocRef = Utility.getCollectionReferenceUnclaimed(itemType).document(docID);
                unclaimedDocRef.update("claimerUserID", GlobalVariables.currentUserID);
                unclaimedDocRef.update("nameOfClaimer", GlobalVariables.name);
                unclaimedDocRef.update("matrixNoOfClaimer", GlobalVariables.matrixNo);
                unclaimedDocRef.update("timestampClaimed", timestampClaimed);
                unclaimedDocRef.update("tutorialOfClaimer", tutorial);
                unclaimedDocRef.update("estimatePrice", price);
                unclaimedDocRef.update("status", "claimed");

                //move data of item to claimed collection
                DocumentReference claimedDocRef = Utility.getCollectionReferenceClaimed(itemType).document(docID);
                unclaimedDocRef.addSnapshotListener(ViewLostItemActivity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot unclaimedDocumentSnapshot, @Nullable FirebaseFirestoreException error) {
                        LostItem claimedItemDetails = new LostItem();

                        claimedItemDetails.setContactInfo(unclaimedDocumentSnapshot.getString("contactInfo"));
                        claimedItemDetails.setEstimatePrice(unclaimedDocumentSnapshot.getString("estimatePrice"));
                        claimedItemDetails.setImageUriStr(unclaimedDocumentSnapshot.getString("imageUriStr"));
                        claimedItemDetails.setItemType(unclaimedDocumentSnapshot.getString("itemType"));
                        claimedItemDetails.setPlace(unclaimedDocumentSnapshot.getString("place"));
                        claimedItemDetails.setStatus("claimed");

                        claimedItemDetails.setClaimerUserID(unclaimedDocumentSnapshot.getString("claimerUserID"));
                        claimedItemDetails.setReporterUserID(unclaimedDocumentSnapshot.getString("reporterUserID"));

                        claimedItemDetails.setMatrixNoOfClaimer(unclaimedDocumentSnapshot.getString("matrixNoOfClaimer"));
                        claimedItemDetails.setMatrixNoOfReporter(unclaimedDocumentSnapshot.getString("matrixNoOfReporter"));

                        claimedItemDetails.setNameOfClaimer(unclaimedDocumentSnapshot.getString("nameOfClaimer"));
                        claimedItemDetails.setNameOfReporter(unclaimedDocumentSnapshot.getString("nameOfReporter"));

                        claimedItemDetails.setTimestampClaimed(unclaimedDocumentSnapshot.getTimestamp("timestampClaimed"));
                        claimedItemDetails.setTimestampReported(unclaimedDocumentSnapshot.getTimestamp("timestampReported"));

                        claimedItemDetails.setTutorialOfClaimer(unclaimedDocumentSnapshot.getString("tutorialOfClaimer"));

                        claimedDocRef.set(claimedItemDetails).addOnSuccessListener(v-> {
                            Utility.showToast(ViewLostItemActivity.this, "Successfully claimed");
                            unclaimedDocRef.delete();
                        });
                    }
                });
                finish();
            }
        });
    }

    boolean isValid(String name, String tutorial, String price){
        if(Objects.equals(name, "")){
            nameEditText.setError("Enter name");
            return false;
        }
        if(Objects.equals(tutorial, "")){
            tutorialEditText.setError("No location");
            return false;
        }
        if(Objects.equals(price, "")){
            estimatePriceEditText.setError("Enter approx. price");
            return false;
        }
        return true;
    }
}