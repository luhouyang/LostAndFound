package com.example.lostfound;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class ReportLostItemActivity extends AppCompatActivity {

    private Uri imageUri;

    RecyclerView recyclerView;
    ImageButton itemTypeDropDown;
    EditText contactInfoEditText, placeEditText;
    TextView itemTypeClickableTextView, itemTypeTextView, reportItemBtnTextView, uploadPhotoTextView;
    ImageView lostItemPic;
    Timestamp timestampReported;
    LostItemAdaptor lostItemAdaptor;

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private String imageUriStr;
    private boolean clicked, photoUpload, infoUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_lost_item);

        recyclerView = findViewById(R.id.report_item_past_items_recycler_view);
        itemTypeDropDown = findViewById(R.id.item_type_drop_down);
        contactInfoEditText = findViewById(R.id.contact_info_edit_text);
        itemTypeClickableTextView = findViewById(R.id.item_type_clickable_text_view);
        itemTypeTextView = findViewById(R.id.item_type_text_view);
        placeEditText = findViewById(R.id.place_edit_text);
        reportItemBtnTextView = findViewById(R.id.report_item_button_text_view);
        uploadPhotoTextView = findViewById(R.id.upload_photo_text_view);
        lostItemPic = findViewById(R.id.lost_item_pic);

        imageUriStr = "NO_URI";
        clicked = false;
        photoUpload = false;
        infoUpload = false;

        imageUri = null;

        ActivityResultLauncher<Intent> launcher=
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                    if(result.getResultCode()==RESULT_OK){
                        Uri uri=result.getData().getData();
                        lostItemPic.setImageURI(uri);
                        // Use the uri to load the image
                        imageUri = uri;
                    }else if(result.getResultCode()==ImagePicker.RESULT_ERROR){
                        Utility.showToast(ReportLostItemActivity.this, ImagePicker.Companion.getError(result.getData()));
                        // Use ImagePicker.Companion.getError(result.getData()) to show an error
                    }
                });

        itemTypeClickableTextView.setOnClickListener(v-> showDropDownMenu());

        //lostItemPic.setOnClickListener(v-> {
        //    if(checkAndRequestPermissions(ReportLostItemActivity.this)){
        //        chooseImage(ReportLostItemActivity.this);
        //    }
        //});
        lostItemPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(ReportLostItemActivity.this)
                        .crop()
                        .maxResultSize(1080, 1080, true)
                        .provider(ImageProvider.BOTH)
                        .createIntentFromDialog((Function1)(new Function1(){
                            public Object invoke(Object var1){
                                this.invoke((Intent)var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it){
                                Intrinsics.checkNotNullParameter(it,"it");
                                launcher.launch(it);
                            }
                        }));
            }
        });
        reportItemBtnTextView.setOnClickListener(v-> {
            if (!clicked){
                clicked = true;
                addLostItemToDatabase();
            }
        });
        setUpRecyclerView("Place-Holder");
    }

    void setUpRecyclerView(String itemType){
        Query query = Utility.getCollectionReferenceUnclaimed(itemType).orderBy("timestampReported", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (querySnapshot.size()!=0){
                    FirestoreRecyclerOptions<LostItem> options = new FirestoreRecyclerOptions.Builder<LostItem>()
                            .setQuery(query, LostItem.class).build();
                    recyclerView.setLayoutManager(new GridLayoutManager(ReportLostItemActivity.this, 2));
                    lostItemAdaptor = new LostItemAdaptor(options, ReportLostItemActivity.this, "reporting");
                    recyclerView.setAdapter(lostItemAdaptor);
                    lostItemAdaptor.startListening();
                    lostItemAdaptor.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                }else{
                    Query defaultQuery = Utility.getCollectionReferenceUnclaimed("Place-Holder").orderBy("timestampReported", Query.Direction.DESCENDING);
                    FirestoreRecyclerOptions<LostItem> options = new FirestoreRecyclerOptions.Builder<LostItem>()
                            .setQuery(defaultQuery, LostItem.class).build();
                    recyclerView.setLayoutManager(new GridLayoutManager(ReportLostItemActivity.this, 2));
                    lostItemAdaptor = new LostItemAdaptor(options, ReportLostItemActivity.this, "reporting");
                    recyclerView.setAdapter(lostItemAdaptor);
                    recyclerView.setVisibility(View.GONE);
                }
            }
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
                lostItemAdaptor.stopListening();
                setUpRecyclerView("Bottles");
                return true;
            }
            if(menuItem.getTitle()=="Calculators"){
                itemTypeTextView.setText("Calculators");
                lostItemAdaptor.stopListening();
                setUpRecyclerView("Calculators");
                return true;
            }
            if(menuItem.getTitle()=="Bags"){
                itemTypeTextView.setText("Bags");
                lostItemAdaptor.stopListening();
                setUpRecyclerView("Bags");
                return true;
            }
            if(menuItem.getTitle()=="Electronic Devices"){
                itemTypeTextView.setText("Electronic Devices");
                lostItemAdaptor.stopListening();
                setUpRecyclerView("Electronic Devices");
                return true;
            }
            if(menuItem.getTitle()=="Miscellaneous"){
                itemTypeTextView.setText("Miscellaneous");
                lostItemAdaptor.stopListening();
                setUpRecyclerView("Miscellaneous");
                return true;
            }
            return false;
        });
    }

    void uploadImageUriToFirestore(String itemType){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference pictureStorageRef = GlobalVariables.storageReference.child("image/" + GlobalVariables.organization + "/" + itemType + "/" + randomKey);
        imageUriStr = "image/" + GlobalVariables.organization + "/" + itemType + "/" + randomKey;
        pictureStorageRef.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        pd.dismiss();
                        Utility.showToast(ReportLostItemActivity.this, "Image Uploaded");
                        photoUpload = true;
                        if(photoUpload && infoUpload){
                            GlobalVariables.reportedItem = true;
                            Utility.showToast(ReportLostItemActivity.this, "Successfully reported item");
                            finish();
                        }
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
    }

    void addLostItemToDatabase(){
        String itemType = itemTypeTextView.getText().toString();
        String contactInfo = contactInfoEditText.getText().toString();
        String place = placeEditText.getText().toString();
        this.timestampReported = Timestamp.now();

        if(!checkInformation(itemType, place)){
            clicked = false;
        }else{
            LostItem lostItem = new LostItem();

            lostItem.setReporterUserID(GlobalVariables.currentUserID);
            lostItem.setNameOfReporter(GlobalVariables.name);
            lostItem.setMatrixNoOfReporter(GlobalVariables.matrixNo);
            lostItem.setItemType(itemType);
            lostItem.setContactInfo(contactInfo);
            lostItem.setTimestampReported(timestampReported);
            lostItem.setPlace(place);
            lostItem.setStatus("unclaimed");

            //Image upload
            if(imageUri!=null){
                uploadImageUriToFirestore(itemType);
            }
            lostItem.setImageUriStr(imageUriStr);

            DocumentReference documentReference = Utility.getCollectionReferenceUnclaimed(itemType).document();
            documentReference.set(lostItem)
                    .addOnCompleteListener(v-> {
                        GlobalVariables.userDataDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot reporterDocumentSnapshot, @Nullable FirebaseFirestoreException error) {
                                int credits = reporterDocumentSnapshot.getLong("credits").intValue();
                                GlobalVariables.credits = credits;
                                infoUpload = true;
                                if(photoUpload && infoUpload){
                                    GlobalVariables.reportedItem = true;
                                    Utility.showToast(ReportLostItemActivity.this, "Successfully reported item");
                                    finish();
                                }
                            }
                        });
                    })
                    .addOnFailureListener(v-> {
                        Utility.showToast(ReportLostItemActivity.this, v.getLocalizedMessage());
                    });
            //startActivity(new Intent(ReportLostItemActivity.this, MainActivity.class));
        }
    }

    boolean checkInformation(String itemType, String place){
        if(Objects.equals(itemType, "Item Type")){
            itemTypeTextView.setError("No item type selected");
            return false;
        }
        if(Objects.equals(place, "")){
            placeEditText.setError("No location");
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
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                    }
                    break;
            }
        }
    }
}