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

    //variables for processing reported item
    LinearLayout viewReportItemLayout;
    TextView returnTextView, imageUriStrReportTextView, timestampReportTextView, placeReportTextView, contactInfoReportTextView, verifyReportTextView;
    ImageView imageViewReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_claimed_item);

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

        //defining process report item variables
        viewReportItemLayout = findViewById(R.id.process_report_item_layout);
        returnTextView = findViewById(R.id.view_return_text_view);
        imageUriStrReportTextView = findViewById(R.id.view_report_image_uri_string);
        timestampReportTextView = findViewById(R.id.view_report_item_timestamp_text_view);
        placeReportTextView = findViewById(R.id.view_report_item_place_text_view);
        contactInfoReportTextView = findViewById(R.id.view_report_contact_info_text_view);
        verifyReportTextView = findViewById(R.id.verify_report_item_text_view);
        imageViewReport =  findViewById(R.id.view_report_item_image_view);

        //display viewing info
        String itemType = getIntent().getStringExtra("itemType");
        String imageUriStr = getIntent().getStringExtra("imageUriStr");
        String localFilePath = getIntent().getStringExtra("localFilePath");
        String timestampReported = getIntent().getStringExtra("timestampReported");
        String place = getIntent().getStringExtra("place");
        String contactInfo = getIntent().getStringExtra("contactInfo");
        String docID = getIntent().getStringExtra("docID");
        String status = getIntent().getStringExtra("status");

        bitmap = BitmapFactory.decodeFile(localFilePath);

        if(Objects.equals(status, "reported")){
            viewItemLayout.setVisibility(View.GONE);
            reportClaimItemLayout.setVisibility(View.GONE);
            viewReportItemLayout.setVisibility(View.VISIBLE);

            imageUriStrReportTextView.setText(imageUriStr);
            imageViewReport.setImageBitmap(bitmap);
            timestampReportTextView.setText(timestampReported);
            placeReportTextView.setText(place);
            contactInfoReportTextView.setText(contactInfo);

            returnTextView.setOnClickListener(u-> {
                finish();
            });
        }else{
            viewItemLayout.setVisibility(View.VISIBLE);
            reportClaimItemLayout.setVisibility(View.GONE);
            viewReportItemLayout.setVisibility(View.GONE);

            itemTypeTextView.setText(itemType);
            imageUriStrTextView.setText(imageUriStr);
            imageView.setImageBitmap(bitmap);
            timestampTextView.setText(timestampReported);
            placeTextView.setText(place);
            contactInfoTextView.setText(contactInfo);

            //functions and buttons
            claimItemTextView.setOnClickListener(v-> {
                viewItemLayout.setVisibility(View.GONE);
                reportClaimItemLayout.setVisibility(View.VISIBLE);
            });
            cancelReportClaimTextView.setOnClickListener(v-> {
                viewItemLayout.setVisibility(View.VISIBLE);
                reportClaimItemLayout.setVisibility(View.GONE);
            });

            confirmReportTextView.setOnClickListener(v-> {
                String tutorial = tutorialEditText.getText().toString();
                String price = estimatePriceEditText.getText().toString();
                if (isValid(nameEditText.getText().toString(), tutorial, price)) {
                    DocumentReference claimedItemDocRef = Utility.getCollectionReferenceClaimed(itemType).document(docID);
                    claimedItemDocRef.update("status", "reported").addOnSuccessListener(q -> {
                        GlobalVariables.userDataDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot complainUserDataSnapshot, @Nullable FirebaseFirestoreException error) {
                                claimedItemDocRef.update("complainUserID", GlobalVariables.currentUserID);
                                claimedItemDocRef.update("nameOfComplain", complainUserDataSnapshot.getString("name"));
                                claimedItemDocRef.update("matrixNoOfComplain", complainUserDataSnapshot.getString("matrixNo"));
                                claimedItemDocRef.update("complainPrice", price);
                                claimedItemDocRef.update("tutorialComplain", tutorial);
                                claimedItemDocRef.update("timestampComplained", timestampReportClaimed);
                                Utility.showToast(ViewClaimedItemActivity.this, "Successfully reported");
                            }
                        });
                        GlobalVariables.complainItem = true;
                    });
                    finish();
                }
            });
        }
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