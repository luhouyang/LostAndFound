package com.example.lostfound;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewLostItemActivity extends AppCompatActivity {

    //variables for viewing item
    LinearLayout viewItemLayout;
    TextView itemTypeTextView, imageUriStrTextView, timestampTextView, contactInfoTextView, claimItemTextView;
    ImageView imageView;
    Bitmap bitmap;

    //variables for claiming item
    LinearLayout claimItemLayout;
    TextView cancelClaimTextView, confirmClaimTextView;
    EditText matrixNumberEditText, nameEditText, tutorialEditText, emailEditText, itemDetailEditText;

    //general variables
    private String itemType, imageUriStr,localFilePath, timestamp, contactInfo, docID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lost_item);

        //defining viewing item variables
        viewItemLayout = findViewById(R.id.view_lost_item_layout);
        itemTypeTextView = findViewById(R.id.view_lost_item_type_text_view);
        imageUriStrTextView = findViewById(R.id.view_lost_image_uri_string);
        timestampTextView = findViewById(R.id.view_lost_item_timestamp_text_view);
        contactInfoTextView = findViewById(R.id.view_lost_contact_info_text_view);
        claimItemTextView = findViewById(R.id.claim_lost_item_type_text_view);
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

        //display viewing info
        itemType = getIntent().getStringExtra("itemType");
        imageUriStr = getIntent().getStringExtra("imageUriStr");
        localFilePath = getIntent().getStringExtra("localFilePath");
        timestamp = getIntent().getStringExtra("timestamp");
        contactInfo = getIntent().getStringExtra("contactInfo");
        docID = getIntent().getStringExtra("docID");

        bitmap = BitmapFactory.decodeFile(localFilePath);

        itemTypeTextView.setText(itemType);
        imageUriStrTextView.setText(imageUriStr);
        imageView.setImageBitmap(bitmap);
        timestampTextView.setText(timestamp);
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
    }
}