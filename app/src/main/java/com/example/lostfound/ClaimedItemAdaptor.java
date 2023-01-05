package com.example.lostfound;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ClaimedItemAdaptor extends FirestoreRecyclerAdapter<LostItem, ClaimedItemAdaptor.ClaimedItemViewHolder> {
    Context context;
    String localFilePath;
    String key;
    boolean adminView;

    StorageReference storageReference;

    public ClaimedItemAdaptor(@NonNull FirestoreRecyclerOptions<LostItem> options, Context context, String key, boolean adminView) {
        super(options);
        this.context = context;
        this.key = key;
        this.adminView = adminView;
    }

    @Override
    protected void onBindViewHolder(@NonNull ClaimedItemAdaptor.ClaimedItemViewHolder holder, int position, @NonNull LostItem lostItem) {
        holder.itemTypeTextView.setText(lostItem.itemType);
        holder.imageUriString.setText(lostItem.imageUriStr);
        storageReference = GlobalVariables.firebaseStorage.getReference(lostItem.imageUriStr);
        try {
            File localFile = File.createTempFile("image", ".jpg");
            storageReference.getFile(localFile).addOnSuccessListener(v-> {
                localFilePath = localFile.getAbsolutePath();
                Bitmap bitmap = BitmapFactory.decodeFile(localFilePath);
                holder.imageView.setImageBitmap(bitmap);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.timestampTextView.setText(Utility.timeToString(lostItem.timestampReported));
        holder.placeTextView.setText(lostItem.place);
        holder.contactInfoTextView.setText(lostItem.contactInfo);

        if(lostItem.status.equals("reported")){
            int red = ContextCompat.getColor(this.context, R.color.red_bright);
            holder.linearLayout.setBackgroundColor(red);
            if (Objects.equals(this.key, "admin") && this.adminView){
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ViewClaimedItemActivity.class);
                    intent.putExtra("itemType", lostItem.itemType);
                    intent.putExtra("imageUriStr", lostItem.imageUriStr);
                    intent.putExtra("localFilePath", localFilePath);
                    intent.putExtra("timestampReported", Utility.timeToString(lostItem.timestampReported));
                    intent.putExtra("place", lostItem.place);
                    intent.putExtra("contactInfo", lostItem.contactInfo);
                    intent.putExtra("claimerID", lostItem.claimerUserID);
                    String docID = this.getSnapshots().getSnapshot(position).getId();
                    intent.putExtra("docID", docID);
                    intent.putExtra("status", lostItem.status);
                    context.startActivity(intent);
                });
            }
        }else if(lostItem.status.equals("resolved")){
            int green = ContextCompat.getColor(this.context, R.color.green_bright);
            holder.linearLayout.setBackgroundColor(green);
        }else{
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewClaimedItemActivity.class);
                intent.putExtra("itemType", lostItem.itemType);
                intent.putExtra("imageUriStr", lostItem.imageUriStr);
                intent.putExtra("localFilePath", localFilePath);
                intent.putExtra("timestampReported", Utility.timeToString(lostItem.timestampReported));
                intent.putExtra("place", lostItem.place);
                intent.putExtra("contactInfo", lostItem.contactInfo);
                intent.putExtra("claimerID", lostItem.claimerUserID);
                String docID = this.getSnapshots().getSnapshot(position).getId();
                intent.putExtra("docID", docID);
                intent.putExtra("status", lostItem.status);
                context.startActivity(intent);
            });
        }
    }

    @NonNull
    @Override
    public ClaimedItemAdaptor.ClaimedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_lost_item, parent, false);
        return new ClaimedItemViewHolder(view);
    }

    static class ClaimedItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView itemTypeTextView, contactInfoTextView, timestampTextView, imageUriString, placeTextView;
        ImageView imageView;

        public ClaimedItemViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.recycler_linear_layout);
            itemTypeTextView = itemView.findViewById(R.id.recycler_unclaimed_item_type_text_view);
            imageUriString = itemView.findViewById(R.id.recycler_image_uri_string);
            imageView = itemView.findViewById(R.id.unclaimed_item_image_view);
            contactInfoTextView = itemView.findViewById(R.id.recycler_unclaimed_contact_info_text_view);
            timestampTextView = itemView.findViewById(R.id.recycler_unclaimed_item_timestamp_text_view);
            placeTextView = itemView.findViewById(R.id.recycler_unclaimed_place_text_view);
        }
    }
}
