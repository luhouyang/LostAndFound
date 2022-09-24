package com.example.lostfound;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;

public class ReportLostItemActivity extends AppCompatActivity {

    ImageButton itemTypeDropDown;
    EditText contactInfoEditText;
    TextView itemTypeTextView, reportItemBtnTextView, uploadPhotoTextView;
    ImageView lostItemPic;
    Timestamp timestamp;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String imageUriStr;

    public Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_lost_item);

        itemTypeDropDown = findViewById(R.id.item_type_drop_down);
        contactInfoEditText = findViewById(R.id.contact_info_edit_text);
        itemTypeTextView = findViewById(R.id.item_type_text_view);
        reportItemBtnTextView = findViewById(R.id.report_item_button_text_view);
        uploadPhotoTextView = findViewById(R.id.upload_photo_text_view);
        lostItemPic = findViewById(R.id.lost_item_pic);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        imageUriStr = "NO_URI";

        imageUri = null;

        itemTypeDropDown.setOnClickListener(v-> showDropDownMenu());
        itemTypeTextView.setOnClickListener(v-> showDropDownMenu());
        lostItemPic.setOnClickListener(view-> choosePicture());
        reportItemBtnTextView.setOnClickListener(v-> addLostItemToDatabase());
    }

    void showDropDownMenu(){
        PopupMenu popupMenu = new PopupMenu(ReportLostItemActivity.this, itemTypeDropDown);
        popupMenu.getMenu().add("Bottles");
        popupMenu.getMenu().add("Calculators");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getTitle()=="Bottles"){
                itemTypeTextView.setText("Bottles");
                return true;
            }
            if(menuItem.getTitle()=="Calculators"){
                itemTypeTextView.setText("Calculators");
                return true;
            }
            return false;
        });
    }

    void choosePicture(){
        Intent intent = new Intent();
        intent.setType("image/**");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            lostItemPic.setImageURI(imageUri);
        }
    }

    void uploadImageUriToFirestore(String itemType){

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference pictureStorageRef = storageReference.child("image/" + itemType + "/" + randomKey);
        imageUriStr = "image/" + itemType + "/" + randomKey;
        pictureStorageRef.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        pd.dismiss();
                        Utility.showToast(ReportLostItemActivity.this, "Image Uploaded");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Utility.showToast(ReportLostItemActivity.this, "Image Upload Failed");
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercentage = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Progress: " + (int) progressPercentage + "%");
                    }
                });
        return;
    }

    void addLostItemToDatabase(){
        String itemType = itemTypeTextView.getText().toString();
        String contactInfo = contactInfoEditText.getText().toString();
        this.timestamp = Timestamp.now();

        if(!checkInformation(itemType, contactInfo)){
            return;
        }else{
            LostItem lostItem = new LostItem();
            lostItem.setItemType(itemType);
            lostItem.setContactInfo(contactInfo);
            lostItem.setTimestamp(timestamp);

            //Image upload
            if(imageUri!=null){
                uploadImageUriToFirestore(itemType);
            }
            lostItem.setImageUriStr(imageUriStr);

            DocumentReference documentReference = Utility.getCollectionReferenceUnclaimed(itemType).document();
            documentReference.set(lostItem)
                    .addOnCompleteListener(v-> Utility.showToast(ReportLostItemActivity.this, "Successfully reported item"))
                    .addOnFailureListener(v-> Utility.showToast(ReportLostItemActivity.this, v.getLocalizedMessage()));
            //startActivity(new Intent(ReportLostItemActivity.this, MainActivity.class));
            finish();
        }
    }

    boolean checkInformation(String itemType, String contactInfo){
        if(Objects.equals(itemType, "Item Type")){
            itemTypeTextView.setError("No item type selected");
            return false;
        }
        if(Objects.equals(contactInfo, "")){
            contactInfoEditText.setError("No contact info");
            return false;
        }
        if(imageUri==null){
            uploadPhotoTextView.setError("No photo");
            return false;
        }
        return true;
    }
}