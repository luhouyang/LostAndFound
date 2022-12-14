package com.example.lostfound;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextView reportItemTextView, unclaimedItemTextView, claimedItemTextView;
    ImageButton menuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        popupMenu.getMenu().add("Profile");
        popupMenu.getMenu().add("Logout");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
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
        GlobalVariables.matrixNo = null;
        GlobalVariables.key = null;
        GlobalVariables.credits = 0;
        GlobalVariables.organization = null;
        GlobalVariables.firebaseStorage = null;
        GlobalVariables.storageReference = null;
        GlobalVariables.reportedItem = false;
        GlobalVariables.complainItem = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(GlobalVariables.reportedItem){
            GlobalVariables.credits += 1;
            GlobalVariables.userDataDocRef.update("credits", GlobalVariables.credits)
                    .addOnSuccessListener(v-> {
                        GlobalVariables.reportedItem=false;
                    });
        }
    }
}