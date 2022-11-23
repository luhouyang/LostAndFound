package com.example.lostfound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView reportItemTextView, unclaimedItemTextView, claimedItemTextView;
    ImageButton menuBtn;

    Bitmap bitmap;
    String localFilePath;

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        reportItemTextView = findViewById(R.id.report_lost_items_text_view);
        unclaimedItemTextView = findViewById(R.id.unclaimed_items_text_view);
        claimedItemTextView = findViewById(R.id.claimed_items_text_view);
        menuBtn = findViewById(R.id.main_menu_btn);

        reportItemTextView.setOnClickListener(v-> startActivity(new Intent(MainActivity.this, ReportLostItemActivity.class)));
        unclaimedItemTextView.setOnClickListener(v-> startActivity(new Intent(MainActivity.this, UnclaimedItemActivity.class)));
        claimedItemTextView.setOnClickListener(v-> startActivity(new Intent(MainActivity.this, ClaimedItemActivity.class)));
        menuBtn.setOnClickListener(v-> showMenu());
    }

    @Override
    public void onBackPressed(){
        final CharSequence[] optionsMenu = {"Close App", "Return"}; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Close App")){
                    clearGlobalVariables();
                    finish();
                }
                else if (optionsMenu[i].equals("Return")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    void showMenu(){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menuBtn);
        //popupMenu.getMenu().add("Debug");
        popupMenu.getMenu().add("Profile");
        popupMenu.getMenu().add("Logout");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //if(menuItem.getTitle()=="Debug"){
                //    createPDF("bronze");
                //    if(checkAndRequestPermissions(MainActivity.this)){createPDF("bronze");}
                //    return true;
                //}
                if(menuItem.getTitle()=="Profile"){
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
                }
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    clearGlobalVariables();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    void clearGlobalVariables(){
        GlobalVariables.currentUser = null;
        GlobalVariables.currentUserID = null;
        GlobalVariables.userDataDocRef = null;
        GlobalVariables.name = null;
        GlobalVariables.identification = null;
        GlobalVariables.userCodeNo = null;
        GlobalVariables.matrixNo = null;
        GlobalVariables.key = null;
        GlobalVariables.credits = 0;
        GlobalVariables.organization = null;
        GlobalVariables.firebaseStorage = null;
        GlobalVariables.storageReference = null;
        GlobalVariables.reportedItem = false;
    }

    void createPDF(String type){
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(2000, 1414, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Paint paint = new Paint();
        if(Objects.equals(type, "bronze")){
            StorageReference storageReference = GlobalVariables.firebaseStorage.getReference("image/bronze");
            try {
                File localFile = File.createTempFile("image", ".jpg");
                storageReference.getFile(localFile).addOnSuccessListener(v-> {
                    localFilePath = localFile.getAbsolutePath();
                    bitmap = BitmapFactory.decodeFile(localFilePath);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Rect rectangle = new Rect(0,0,2000,1414);
        page.getCanvas().drawBitmap(bitmap, null, rectangle, null);
        page.getCanvas().drawText(GlobalVariables.name, 1000, 1000, paint);
        pdfDocument.finishPage(page);

        String pdfFilePath = Environment.getExternalStorageDirectory().getPath() + "/" + type + ".pdf";
        File file = new File(pdfFilePath);
        Uri uri = Uri.fromFile(new File(pdfFilePath));
        //try {
        //    pdfDocument.writeTo(new FileOutputStream(file));
        //} catch (Exception e){
        //    e.printStackTrace();
        //}
        GlobalVariables.storageReference.putFile(uri);

        pdfDocument.close();
    }

    // function to check permission
    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            //ActivityCompat.requestPermissions(context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);

            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                    .toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
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
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    createPDF("bronze");
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(GlobalVariables.reportedItem){
            GlobalVariables.credits += 1;
            GlobalVariables.userDataDocRef.update("credits", GlobalVariables.credits)
                    .addOnSuccessListener(v-> {
                        GlobalVariables.reportedItem=false;
                        if(GlobalVariables.credits >= 100){
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                        }
                    });
        }
    }
}