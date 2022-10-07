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

public class ViewClaimedItemActivity extends AppCompatActivity {

    //variables for viewing claimed item
    LinearLayout viewItemLayout;
    TextView itemTypeTextView, imageUriStrTextView, timestampTextView, contactInfoTextView, claimItemTextView;
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
    private String itemType, imageUriStr,localFilePath, timestampReported, contactInfo, docID;

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
            reportClaimItemLayout.setVisibility(reportClaimItemLayout.VISIBLE);
        });
        cancelReportClaimTextView.setOnClickListener(v-> {
            viewItemLayout.setVisibility(viewItemLayout.VISIBLE);
            reportClaimItemLayout.setVisibility(reportClaimItemLayout.GONE);
        });

        confirmReportTextView.setOnClickListener(v-> {
            Utility.showToast(ViewClaimedItemActivity.this, "Successfully reported");
            finish();
        });
    }
}