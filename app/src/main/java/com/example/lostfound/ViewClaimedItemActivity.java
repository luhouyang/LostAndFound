package com.example.lostfound;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import java.util.Objects;

public class ViewClaimedItemActivity extends AppCompatActivity {

    //variables for viewing claimed item
    LinearLayout viewItemLayout;
    TextView itemTypeTextView, imageUriStrTextView, timestampTextView, placeTextView, contactInfoTextView, claimItemTextView;
    ImageView imageView;
    Bitmap bitmap;

    //variables for reporting claimed item
    LinearLayout reportClaimItemLayout;
    TextView cancelReportClaimTextView, confirmReportTextView;
    EditText matrixNumberEditText, nameEditText, tutorialEditText, emailEditText, itemDetailEditText, estimatePriceEditText;
    Timestamp timestampReportClaimed;

    //general variables
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;
    private String itemType, imageUriStr,localFilePath, timestampReported, place, contactInfo, docID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_claimed_item);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //defining viewing item variables
        viewItemLayout = findViewById(R.id.view_claimed_item_layout);
        itemTypeTextView = findViewById(R.id.view_claimed_item_type_text_view);
        imageUriStrTextView = findViewById(R.id.view_claimed_image_uri_string);
        timestampTextView = findViewById(R.id.view_claimed_item_timestamp_text_view);
        placeTextView = findViewById(R.id.view_claimed_item_place_text_view);
        contactInfoTextView = findViewById(R.id.view_claimed_contact_info_text_view);
        claimItemTextView = findViewById(R.id.report_claimed_item_text_view);
        imageView =  findViewById(R.id.view_claimed_item_image_view);

        //defining claim item variables
        reportClaimItemLayout = findViewById(R.id.report_claimed_item_layout);
        cancelReportClaimTextView = findViewById(R.id.cancel_report_item_text_view);
        confirmReportTextView = findViewById(R.id.confirm_report_claimed_item_text_view);
        matrixNumberEditText = findViewById(R.id.report_matrix_number_edit_text);
        nameEditText = findViewById(R.id.report_name_edit_text);
        tutorialEditText = findViewById(R.id.report_tutorial_edit_text);
        emailEditText = findViewById(R.id.report_email_edit_text);
        itemDetailEditText = findViewById(R.id.report_item_description_edit_text);
        estimatePriceEditText = findViewById(R.id.report_item_price_edit_text);
        timestampReportClaimed = Timestamp.now();

        //display viewing info
        itemType = getIntent().getStringExtra("itemType");
        imageUriStr = getIntent().getStringExtra("imageUriStr");
        localFilePath = getIntent().getStringExtra("localFilePath");
        timestampReported = getIntent().getStringExtra("timestampReported");
        place = getIntent().getStringExtra("place");
        contactInfo = getIntent().getStringExtra("contactInfo");
        docID = getIntent().getStringExtra("docID");

        bitmap = BitmapFactory.decodeFile(localFilePath);

        itemTypeTextView.setText(itemType);
        imageUriStrTextView.setText(imageUriStr);
        imageView.setImageBitmap(bitmap);
        timestampTextView.setText(timestampReported);
        placeTextView.setText(place);
        contactInfoTextView.setText(contactInfo);

        //functions and buttons
        claimItemTextView.setOnClickListener(v-> {
            viewItemLayout.setVisibility(viewItemLayout.GONE);
            reportClaimItemLayout.setVisibility(reportClaimItemLayout.VISIBLE);
        });
        cancelReportClaimTextView.setOnClickListener(v-> {
            viewItemLayout.setVisibility(viewItemLayout.VISIBLE);
            reportClaimItemLayout.setVisibility(reportClaimItemLayout.GONE);
        });

        confirmReportTextView.setOnClickListener(v-> {
            String tutorial = tutorialEditText.getText().toString();
            String price = estimatePriceEditText.getText().toString();
            if (isValid(nameEditText.getText().toString(), tutorial, price)) {
                DocumentReference claimedItemDocRef = Utility.getCollectionReferenceClaimed(itemType).document(docID);
                claimedItemDocRef.update("status", "reported").addOnSuccessListener(q -> {
                    DocumentReference complainUserData = Utility.getDocumentReferenceUserData(currentUser);
                    complainUserData.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot complainUserDataSnapshot, @Nullable FirebaseFirestoreException error) {
                            claimedItemDocRef.update("nameOfComplain", complainUserDataSnapshot.getString("name"));
                            claimedItemDocRef.update("matrixNoOfComplain", complainUserDataSnapshot.getString("matrixNo"));
                            claimedItemDocRef.update("complainPrice", price);
                            claimedItemDocRef.update("tutorialComplain", tutorial);
                            claimedItemDocRef.update("timestampComplained", timestampReportClaimed);
                            Utility.showToast(ViewClaimedItemActivity.this, "Successfully reported");
                        }
                    });
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