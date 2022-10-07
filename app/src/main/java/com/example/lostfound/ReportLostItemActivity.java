package com.example.lostfound;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ReportLostItemActivity extends AppCompatActivity {

    public Uri imageUri;

    ImageButton itemTypeDropDown;
    EditText contactInfoEditText;
    TextView itemTypeTextView, reportItemBtnTextView, uploadPhotoTextView;
    ImageView lostItemPic;
    Timestamp timestampReported;

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private String imageUriStr;

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
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        imageUriStr = "NO_URI";

        imageUri = null;

        itemTypeDropDown.setOnClickListener(v-> showDropDownMenu());
        itemTypeTextView.setOnClickListener(v-> showDropDownMenu());
        //lostItemPic.setOnClickListener(view-> choosePicture());
        lostItemPic.setOnClickListener(v-> {
            if(checkAndRequestPermissions(ReportLostItemActivity.this)){
                chooseImage(ReportLostItemActivity.this);
            }
        });
        reportItemBtnTextView.setOnClickListener(v-> {
            reportItemBtnTextView.setOnClickListener(null);
            addLostItemToDatabase();
        });
    }

    void showDropDownMenu(){
        PopupMenu popupMenu = new PopupMenu(ReportLostItemActivity.this, itemTypeDropDown);
        popupMenu.getMenu().add("Bottles");
        popupMenu.getMenu().add("Calculators");
        popupMenu.getMenu().add("Bags");
        popupMenu.getMenu().add("Electronic Devices");
        popupMenu.getMenu().add("Miscellaneous");
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
            if(menuItem.getTitle()=="Bags"){
                itemTypeTextView.setText("Bags");
                return true;
            }
            if(menuItem.getTitle()=="Electronic Devices"){
                itemTypeTextView.setText("Electronic Devices");
                return true;
            }
            if(menuItem.getTitle()=="Miscellaneous"){
                itemTypeTextView.setText("Miscellaneous");
                return true;
            }
            return false;
        });
    }

    //void choosePicture(){
    //    Intent intent = new Intent();
    //    intent.setType("Image/**");
    //    intent.setAction(Intent.ACTION_GET_CONTENT);
    //    startActivityForResult(intent, 1);
    //}

    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
    //    if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
    //        imageUri = data.getData();
    //        lostItemPic.setImageURI(imageUri);
    //    }
    //}

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
        this.timestampReported = Timestamp.now();

        if(!checkInformation(itemType, contactInfo)){
            reportItemBtnTextView.setOnClickListener(v-> {
                reportItemBtnTextView.setOnClickListener(null);
                addLostItemToDatabase();
            });
            return;
        }else{
            DocumentReference reporterDataDocRef = Utility.getDocumentReferenceUserData(currentUser);
            reporterDataDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    String nameOfReporter = documentSnapshot.getString("name");
                    String matrixNoOfReporter = documentSnapshot.getString("matrixNo");

                    LostItem lostItem = new LostItem();

                    lostItem.setNameOfReporter(nameOfReporter);
                    lostItem.setMatrixNoOfReporter(matrixNoOfReporter);
                    lostItem.setItemType(itemType);
                    lostItem.setContactInfo(contactInfo);
                    lostItem.setTimestampReported(timestampReported);

                    //Image upload
                    if(imageUri!=null){
                        uploadImageUriToFirestore(itemType);
                    }
                    lostItem.setImageUriStr(imageUriStr);

                    DocumentReference documentReference = Utility.getCollectionReferenceUnclaimed(itemType).document();
                    documentReference.set(lostItem)
                            .addOnCompleteListener(v-> {
                                int credits = documentSnapshot.getLong("credits").intValue();
                                credits += 1;
                                reporterDataDocRef.update("credits", credits);
                                Utility.showToast(ReportLostItemActivity.this, "Successfully reported item");
                                finish();
                            })
                            .addOnFailureListener(v-> {
                                Utility.showToast(ReportLostItemActivity.this, v.getLocalizedMessage());
                                finish();
                            });
                    startActivity(new Intent(ReportLostItemActivity.this, MainActivity.class));
                }
            });
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
    
    // function to let's the user to choose image from camera or gallery
    private void chooseImage(Context context){
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    // Open the camera and get the photo
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    // function to check permission
    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    // Handled permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(ReportLostItemActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                                    "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();
                } else if (ContextCompat.checkSelfPermission(ReportLostItemActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    chooseImage(ReportLostItemActivity.this);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        lostItemPic.setImageBitmap(selectedImage);
                        imageUri = Utility.getImageUri(this, selectedImage);
                        //lostItemPic.setImageURI(imageUri);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                lostItemPic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }
                        imageUri = data.getData();
                        //lostItemPic.setImageURI(imageUri);
                    }
                    break;
            }
        }
    }
}