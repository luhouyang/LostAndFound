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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class LostItemAdaptor extends FirestoreRecyclerAdapter<LostItem, LostItemAdaptor.LostItemViewHolder> {
    Context context;
    String localFilePath;

    StorageReference storageReference;

    public LostItemAdaptor(@NonNull FirestoreRecyclerOptions<LostItem> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull LostItemViewHolder holder, int position, @NonNull LostItem lostItem) {
        holder.itemTypeTextView.setText(lostItem.itemType);

        holder.imageUriString.setText(lostItem.imageUriStr);
        storageReference = FirebaseStorage.getInstance().getReference(lostItem.imageUriStr);
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

        holder.timestampTextView.setText(Utility.timeToString(lostItem.timestamp));
        holder.contactInfoTextView.setText(lostItem.contactInfo);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewLostItemActivity.class);
            intent.putExtra("itemType", lostItem.itemType);
            intent.putExtra("imageUriStr", lostItem.imageUriStr);
            intent.putExtra("localFilePath", localFilePath);
            intent.putExtra("timestamp", Utility.timeToString(lostItem.timestamp));
            intent.putExtra("contactInfo", lostItem.contactInfo);
            String docID = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docID", docID);
            context.startActivity(intent);
        });
        //holder.itemView.setOnClickListener(v-> Utility.debug(context));
    }

    @NonNull
    @Override
    public LostItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_lost_item, parent, false);
        return new LostItemViewHolder(view);
    }

    class LostItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemTypeTextView, contactInfoTextView, timestampTextView, imageUriString;
        ImageView imageView;

        public LostItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemTypeTextView = itemView.findViewById(R.id.recycler_unclaimed_item_type_text_view);
            imageUriString = itemView.findViewById(R.id.recycler_image_uri_string);
            imageView = itemView.findViewById(R.id.unclaimed_item_image_view);
            contactInfoTextView = itemView.findViewById(R.id.recycler_unclaimed_contact_info_text_view);
            timestampTextView = itemView.findViewById(R.id.recycler_unclaimed_item_timestamp_text_view);
        }
    }
}
