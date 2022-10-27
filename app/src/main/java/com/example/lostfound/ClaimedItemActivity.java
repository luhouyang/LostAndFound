package com.example.lostfound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class ClaimedItemActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwitchCompat adminSwitch;
    TextView itemTypeClickableTextView, itemTypeTextView;
    ImageButton itemTypeDropDownBtn;
    ClaimedItemAdaptor claimedItemAdaptor;

    private boolean adminView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claimed_item);

        recyclerView = findViewById(R.id.recycler_view);
        adminSwitch = findViewById(R.id.admin_switch);
        itemTypeClickableTextView = findViewById(R.id.claimed_item_type_clickable_text_view);
        itemTypeTextView = findViewById(R.id.claimed_item_type_text_view);
        itemTypeDropDownBtn = findViewById(R.id.claimed_item_type_drop_down);

        adminView = false;
        if(Objects.equals(GlobalVariables.key, "admin")){
            adminSwitch.setVisibility(View.VISIBLE);
        }

        adminSwitch.setOnClickListener(v-> {
            adminView = adminSwitch.isChecked();
            claimedItemAdaptor.stopListening();
            setUpRecyclerView(itemTypeTextView.getText().toString());
            claimedItemAdaptor.startListening();
            claimedItemAdaptor.notifyDataSetChanged();
        });
        itemTypeClickableTextView.setOnClickListener(v-> showDropDownMenu());
        //itemTypeTextView.setOnClickListener(v-> showDropDownMenu());
        //itemTypeDropDownBtn.setOnClickListener(v-> showDropDownMenu());
        setUpRecyclerView("Place-Holder");
    }

    void showDropDownMenu(){
        PopupMenu popupMenu = new PopupMenu(ClaimedItemActivity.this, itemTypeDropDownBtn);
        popupMenu.getMenu().add("Bottles");
        popupMenu.getMenu().add("Calculators");
        popupMenu.getMenu().add("Bags");
        popupMenu.getMenu().add("Electronic Devices");
        popupMenu.getMenu().add("Miscellaneous");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getTitle()=="Bottles"){
                itemTypeTextView.setText("Bottles");
                claimedItemAdaptor.stopListening();
                setUpRecyclerView("Bottles");
                claimedItemAdaptor.startListening();
                claimedItemAdaptor.notifyDataSetChanged();
                return true;
            }
            if(menuItem.getTitle()=="Calculators"){
                itemTypeTextView.setText("Calculators");
                claimedItemAdaptor.stopListening();
                setUpRecyclerView("Calculators");
                claimedItemAdaptor.startListening();
                claimedItemAdaptor.notifyDataSetChanged();
                return true;
            }
            if(menuItem.getTitle()=="Bags"){
                itemTypeTextView.setText("Bags");
                claimedItemAdaptor.stopListening();
                setUpRecyclerView("Bags");
                claimedItemAdaptor.startListening();
                claimedItemAdaptor.notifyDataSetChanged();
                return true;
            }
            if(menuItem.getTitle()=="Electronic Devices"){
                itemTypeTextView.setText("Electronic Devices");
                claimedItemAdaptor.stopListening();
                setUpRecyclerView("Electronic Devices");
                claimedItemAdaptor.startListening();
                claimedItemAdaptor.notifyDataSetChanged();
                return true;
            }
            if(menuItem.getTitle()=="Miscellaneous"){
                itemTypeTextView.setText("Miscellaneous");
                claimedItemAdaptor.stopListening();
                setUpRecyclerView("Miscellaneous");
                claimedItemAdaptor.startListening();
                claimedItemAdaptor.notifyDataSetChanged();
                return true;
            }
            return false;
        });
    }

    void setUpRecyclerView(String itemType){
        Query query = Utility.getCollectionReferenceClaimed(itemType).orderBy("timestampReported", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<LostItem> options = new FirestoreRecyclerOptions.Builder<LostItem>()
                .setQuery(query, LostItem.class).build();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        claimedItemAdaptor = new ClaimedItemAdaptor(options, this, GlobalVariables.key, adminView);
        recyclerView.setAdapter(claimedItemAdaptor);
    }

    @Override
    protected void onStart() {
        super.onStart();
        claimedItemAdaptor.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        claimedItemAdaptor.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        claimedItemAdaptor.notifyDataSetChanged();
    }
}