package com.example.lostfound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class UnclaimedItemActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView itemTypeTextView;
    ImageButton itemTypeDropDownBtn;
    LostItemAdaptor lostItemAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unclaimed_item);

        recyclerView = findViewById(R.id.recycler_view);
        itemTypeTextView = findViewById(R.id.unclaimed_item_type_text_view);
        itemTypeDropDownBtn = findViewById(R.id.unclaimed_item_type_drop_down);

        itemTypeTextView.setOnClickListener(v-> showDropDownMenu());
        itemTypeDropDownBtn.setOnClickListener(v-> showDropDownMenu());
        setUpRecyclerView("Place-Holder");
    }

    void showDropDownMenu(){
        PopupMenu popupMenu = new PopupMenu(UnclaimedItemActivity.this, itemTypeDropDownBtn);
        popupMenu.getMenu().add("Bottles");
        popupMenu.getMenu().add("Calculators");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getTitle()=="Bottles"){
                itemTypeTextView.setText("Bottles");
                lostItemAdaptor.stopListening();
                setUpRecyclerView("Bottles");
                lostItemAdaptor.startListening();
                lostItemAdaptor.notifyDataSetChanged();
                return true;
            }
            if(menuItem.getTitle()=="Calculators"){
                itemTypeTextView.setText("Calculators");
                lostItemAdaptor.stopListening();
                setUpRecyclerView("Calculators");
                lostItemAdaptor.startListening();
                lostItemAdaptor.notifyDataSetChanged();
                return true;
            }
            return false;
        });
    }

    void setUpRecyclerView(String itemType){
        Query query = Utility.getCollectionReferenceUnclaimed(itemType).orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<LostItem> options = new FirestoreRecyclerOptions.Builder<LostItem>()
                .setQuery(query, LostItem.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lostItemAdaptor = new LostItemAdaptor(options, this);
        recyclerView.setAdapter(lostItemAdaptor);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lostItemAdaptor.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        lostItemAdaptor.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lostItemAdaptor.notifyDataSetChanged();
    }
}