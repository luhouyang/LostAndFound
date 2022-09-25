package com.example.lostfound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextView reportItemTextView, unclaimedItemTextView, itemsYouReportedTextView;
    ImageButton menuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reportItemTextView = findViewById(R.id.report_lost_items_text_view);
        unclaimedItemTextView = findViewById(R.id.unclaimed_items_text_view);
        menuBtn = findViewById(R.id.main_menu_btn);

        reportItemTextView.setOnClickListener(v-> {
            startActivity(new Intent(MainActivity.this, ReportLostItemActivity.class));
            //finish();
        });
        unclaimedItemTextView.setOnClickListener(v-> {
            startActivity(new Intent(MainActivity.this, UnclaimedItemActivity.class));
            //finish();
        });
        menuBtn.setOnClickListener(v-> showMenu());
    }

    void showMenu(){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

}