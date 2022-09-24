package com.example.lostfound;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewLostItemActivity extends AppCompatActivity {

    TextView itemTypeTextView, imageUriStrTextView, timestampTextView, contactInfoTextView, claimItemTextView;
    ImageView imageView;
    Bitmap bitmap;

    private String itemType, imageUriStr,localFilePath, timestamp, contactInfo, docID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lost_item);

        itemTypeTextView = findViewById(R.id.view_lost_item_type_text_view);
        imageUriStrTextView = findViewById(R.id.view_lost_image_uri_string);
        timestampTextView = findViewById(R.id.view_lost_item_timestamp_text_view);
        contactInfoTextView = findViewById(R.id.view_lost_contact_info_text_view);
        claimItemTextView = findViewById(R.id.claim_lost_item_type_text_view);
        imageView =  findViewById(R.id.view_lost_item_image_view);

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
    }
}