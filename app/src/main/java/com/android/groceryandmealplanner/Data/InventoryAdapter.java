package com.android.groceryandmealplanner.Data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.groceryandmealplanner.R;
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {
    private List<InventoryItem> inventoryList;
    private Context context;

    public InventoryAdapter(Context context, List<InventoryItem> inventoryList) {
        this.context = context;
        this.inventoryList = inventoryList;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryList.get(position);

        holder.nameTextView.setText(item.getName());
        holder.expireDateTextView.setText("Expire: " + item.getExpireDate());
        holder.unitTextView.setText("Unit: " + item.getMeasurementUnit());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));

        // Load image with Glide
        fetchImageBasedOnTitle(holder.nameTextView.getText().toString(), holder.imageView);

      //  holder.lastUpdateTextView.setText("Last Update: " + item.getLastUpdate().toDate().toString());

        // Fetch the lastUpdate as a Timestamp
        Timestamp lastUpdateTimestamp = (Timestamp) item.getLastUpdate();
        if (lastUpdateTimestamp != null) {
            Date lastUpdateDate = lastUpdateTimestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = sdf.format(lastUpdateDate);
            holder.lastUpdateTextView.setText("Last Update: " + formattedDate);
        } else {
            holder.lastUpdateTextView.setText("Last Update: Not available");
        }


        holder.incrementButton.setOnClickListener(v -> {
            Toast.makeText(context, "Increment", Toast.LENGTH_SHORT).show();
        });

        holder.decrementButton.setOnClickListener(v -> {
            Toast.makeText(context, "Decrement", Toast.LENGTH_SHORT).show();

        });
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public void removeItem(int position) {
        inventoryList.remove(position);
        notifyItemRemoved(position);
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, expireDateTextView, lastUpdateTextView, unitTextView, quantityTextView;
        ImageView imageView;
        CardView incrementButton, decrementButton;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.itemName);
            expireDateTextView = itemView.findViewById(R.id.itemExpire);
            lastUpdateTextView = itemView.findViewById(R.id.itemLastUpdate);
            unitTextView = itemView.findViewById(R.id.itemMeasurementUnit);
            quantityTextView = itemView.findViewById(R.id.itemQuantity);
            imageView = itemView.findViewById(R.id.itemImage);
            incrementButton = itemView.findViewById(R.id.itemIncrementBtn);
            decrementButton = itemView.findViewById(R.id.itemDecrementBtn);
        }
    }

    private void fetchImageBasedOnTitle(String itemName, ImageView imageView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Convert itemName to lowercase for case-insensitive comparison
        String normalizedItemName = itemName.toLowerCase();

        // Query Firestore for a matching title (case-insensitive)
        db.collection("InventoryImages")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean imageFound = false;

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String title = document.getString("title");

                        if (title != null && title.equalsIgnoreCase(itemName)) {
                            // Image found in Firestore
                            String imageUrl = document.getString("url");

                            // Load the image using Glide or any other image-loading library
                            Glide.with(imageView.getContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.grocery) // Default placeholder while loading
                                    .into(imageView);

                            imageFound = true;
                            break;
                        }
                    }

                    if (!imageFound) {
                        // If no matching title is found, use a default image from local storage
                        imageView.setImageResource(R.drawable.grocery);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error (e.g., network failure)
                    Toast.makeText(imageView.getContext(), "Failed to fetch image: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    // Use default image in case of error
                    imageView.setImageResource(R.drawable.grocery);
                });
    }
}



