package com.android.groceryandmealplanner.Data;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.groceryandmealplanner.R;
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
            showAddItemPopup(item.getId(),item.getName(),item.getExpireDate(),item.getMeasurementUnit(),item.getQuantity(), item.getLowStockAlert());
        });

        holder.decrementButton.setOnClickListener(v -> {
            showAddItemPopup(item.getId(),item.getName(),item.getExpireDate(),item.getMeasurementUnit(),item.getQuantity(), item.getLowStockAlert());
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
        Spinner spinner;

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
            spinner = itemView.findViewById(R.id.sp_status);
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

    private void showAddItemPopup(String documentID, String itemNameRef, String expireDateRef, String measurementUnitRef, int quantityRef, int lowStockAlertRef ) {
        Dialog dialog = new Dialog(context,R.style.DialogStyle);
        dialog.setContentView(R.layout.popup_add_item);

        EditText etItemName = dialog.findViewById(R.id.et_item_name);
        EditText etExpireDate = dialog.findViewById(R.id.et_expire_date);
        Spinner spStatus = dialog.findViewById(R.id.sp_status);
        EditText tvQuantity = dialog.findViewById(R.id.tv_quantity);
        EditText tvLowStockAlert = dialog.findViewById(R.id.tv_low_stock_alert);
        Button btnAddItem = dialog.findViewById(R.id.btn_add_item);

        CardView btnIncreaseQuantity = dialog.findViewById(R.id.btn_increase_quantity);
        CardView btnDecreaseQuantity = dialog.findViewById(R.id.btn_decrease_quantity);
        CardView btnIncreaseAlert = dialog.findViewById(R.id.btn_increase_alert);
        CardView btnDecreaseAlert = dialog.findViewById(R.id.btn_decrease_alert);
        ImageButton btnPickDate = dialog.findViewById(R.id.btn_pick_date);
        ImageButton close = dialog.findViewById(R.id.closePopUpBtn);
        ImageButton info = dialog.findViewById(R.id.infoBtn);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        btnAddItem.setText("Update Data");
        etItemName.setText(itemNameRef);
        etExpireDate.setText(expireDateRef);
        tvQuantity.setText(quantityRef+"");
        if (measurementUnitRef != null) {
            ArrayAdapter adapter = (ArrayAdapter) spStatus.getAdapter();
            int position = adapter.getPosition(measurementUnitRef);
            spStatus.setSelection(position);
        }
        tvLowStockAlert.setText(lowStockAlertRef+"");

        close.setOnClickListener(v -> {
            dialog.dismiss();
        });
        info.setOnClickListener(v -> {
            infoDialog();
        });

        // Initialize variables
        int[] quantity = {0};
        int[] lowStockAlert = {0};


        // Date Picker Logic
        btnPickDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(context,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                        etExpireDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        // Quantity Logic
        btnIncreaseQuantity.setOnClickListener(v -> adjustQuantity(spStatus, quantity, tvQuantity, true, tvLowStockAlert, lowStockAlert));
        btnDecreaseQuantity.setOnClickListener(v -> adjustQuantity(spStatus, quantity, tvQuantity, false, tvLowStockAlert, lowStockAlert));

        // Low Stock Alert Logic
        btnIncreaseAlert.setOnClickListener(v -> adjustAlert(lowStockAlert, quantity[0], tvLowStockAlert, true));
        btnDecreaseAlert.setOnClickListener(v -> adjustAlert(lowStockAlert, quantity[0], tvLowStockAlert, false));

        // Add Item Button Logic
        btnAddItem.setOnClickListener(v -> {
            String itemName = etItemName.getText().toString().trim();
            String expireDate = etExpireDate.getText().toString().trim();
            String measurementUnit = spStatus.getSelectedItem().toString();
            int quantity1 = Integer.parseInt(tvQuantity.getText().toString());
            int lowStockAlert1 = Integer.parseInt(tvLowStockAlert.getText().toString());

            if (itemName.isEmpty() || expireDate.isEmpty() || quantity1 == 0 || lowStockAlert1 == 0) {
                Toast.makeText(context, "Please fill all fields and set valid values", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call the function to save to Firestore
            updateItemInFirestore(documentID,itemName, expireDate, measurementUnit, quantity1, lowStockAlert1);
            dialog.dismiss();

        });
    }
    private void updateItemInFirestore(String documentID, String itemName, String expireDate, String measurementUnit, int quantity, int lowStockAlert) {
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get current user's UID
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a map for the updated inventory item
        Map<String, Object> updatedItemData = new HashMap<>();
        updatedItemData.put("name", itemName);
        updatedItemData.put("expireDate", expireDate);
        updatedItemData.put("measurementUnit", measurementUnit);
        updatedItemData.put("quantity", quantity);
        updatedItemData.put("lowStockAlert", lowStockAlert);
        updatedItemData.put("lastUpdate", Timestamp.now());

        // Update the item in the user's inventory subcollection using the document ID
        db.collection("Users")
                .document(userID)
                .collection("Inventory")
                .document(documentID) // Specify the document ID
                .update(updatedItemData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Item updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void infoDialog(){
        AlertDialog.Builder secondBuilder = new AlertDialog.Builder(context);
        View secondPopupView = LayoutInflater.from(context).inflate(R.layout.info_popup_dialog, null);
        secondBuilder.setView(secondPopupView);

        AlertDialog secondDialog = secondBuilder.create();
        secondDialog.show();

        // Handle logic for the second dialog here
        ImageButton closeSecondDialog = secondPopupView.findViewById(R.id.closePopUpBtnInfo); // Replace with your button ID
        closeSecondDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondDialog.dismiss(); // Close the second dialog
            }
        });
    }

    private void adjustQuantity(Spinner spStatus, int[] quantity, EditText etQuantity, boolean increase, EditText tvLowStockAlert, int[] lowStockAlert) {
        String status = spStatus.getSelectedItem().toString();
        int step = 1; // Default step for Kilogram

        // Get the current quantity value from EditText
        String currentQuantityText = etQuantity.getText().toString().trim();
        int currentQuantity = 0;

        try {
            currentQuantity = Integer.parseInt(currentQuantityText); // Parse the current quantity value
        } catch (NumberFormatException e) {
            currentQuantity = 0; // Default to 0 on invalid input
        }

        // Get the low stock alert value from EditText
        String lowStockAlertText = tvLowStockAlert.getText().toString().trim();
        int currentLowStockAlert = 0;

        try {
            currentLowStockAlert = Integer.parseInt(lowStockAlertText); // Parse the low stock alert value
        } catch (NumberFormatException e) {
            currentLowStockAlert = 0; // Default to 0 on invalid input
        }

        // Adjust the quantity value
        if (increase) {
            quantity[0] = currentQuantity + step;
        } else {
            quantity[0] = Math.max(currentQuantity - step, 0);
            // If the quantity matches the low stock alert value, decrease the low stock alert value
            if (quantity[0] <= currentLowStockAlert && currentLowStockAlert > 0) {
                lowStockAlert[0] = Math.max(currentLowStockAlert - step, 0);
                tvLowStockAlert.setText(String.valueOf(lowStockAlert[0]));
            }
        }

        // Update the quantity EditText with the adjusted value
        etQuantity.setText(String.valueOf(quantity[0]));
    }


    private void adjustAlert(int[] lowStockAlert, int maxQuantity, EditText tvLowStockAlert, boolean increase) {
        // Get the current low stock alert value from the EditText
        String currentAlertText = tvLowStockAlert.getText().toString().trim();
        int currentAlertValue = 0;

        try {
            currentAlertValue = Integer.parseInt(currentAlertText); // Parse the value
        } catch (NumberFormatException e) {
            currentAlertValue = lowStockAlert[0]; // Use the stored value if parsing fails
        }

        // Update the alert value based on the increase or decrease action
        if (increase) {
            if (currentAlertValue < maxQuantity) {
                currentAlertValue++;
            }
        } else {
            if (currentAlertValue > 0) {
                currentAlertValue--;
            }
        }

        // Synchronize the new value with the array
        lowStockAlert[0] = currentAlertValue;

        // Update the EditText with the new value
        tvLowStockAlert.setText(String.valueOf(currentAlertValue));
    }

}



