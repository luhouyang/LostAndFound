package com.example.lostfound;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class LostItemAdaptor extends FirestoreRecyclerAdapter<LostItem, LostItemAdaptor.LostItemViewHolder> {
    Context context;
    String localFilePath;
    String viewMode;

    StorageReference storageReference;

    public LostItemAdaptor(@NonNull FirestoreRecyclerOptions<LostItem> options, Context context, String viewMode) {
        super(options);
        this.context = context;
        this.viewMode = viewMode;
    }

    @Override
    protected void onBindViewHolder(@NonNull LostItemViewHolder holder, int position, @NonNull LostItem lostItem) {
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

        if (!Objects.equals(this.viewMode, "reporting")){
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewLostItemActivity.class);
                intent.putExtra("itemType", lostItem.itemType);
                intent.putExtra("imageUriStr", lostItem.imageUriStr);
                intent.putExtra("localFilePath", localFilePath);
                intent.putExtra("timestampReported", Utility.timeToString(lostItem.timestampReported));
                intent.putExtra("place", lostItem.place);
                intent.putExtra("contactInfo", lostItem.contactInfo);
                String docID = this.getSnapshots().getSnapshot(position).getId();
                intent.putExtra("docID", docID);
                context.startActivity(intent);
            });
        }
    }

    @NonNull
    @Override
    public LostItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_lost_item, parent, false);
        return new LostItemViewHolder(view);
    }

    static class LostItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemTypeTextView, contactInfoTextView, timestampTextView, imageUriString, placeTextView;
        ImageView imageView;

        public LostItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemTypeTextView = itemView.findViewById(R.id.recycler_unclaimed_item_type_text_view);
            imageUriString = itemView.findViewById(R.id.recycler_image_uri_string);
            imageView = itemView.findViewById(R.id.unclaimed_item_image_view);
            contactInfoTextView = itemView.findViewById(R.id.recycler_unclaimed_contact_info_text_view);
            timestampTextView = itemView.findViewById(R.id.recycler_unclaimed_item_timestamp_text_view);
            placeTextView = itemView.findViewById(R.id.recycler_unclaimed_place_text_view);
        }
    }
}
