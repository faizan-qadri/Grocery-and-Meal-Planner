package com.android.groceryandmealplanner.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.groceryandmealplanner.Data.InventoryAdapter;
import com.android.groceryandmealplanner.Data.InventoryItem;
import com.android.groceryandmealplanner.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Inventory extends Fragment {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<InventoryItem> inventoryList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        CardView fabAddItem = view.findViewById(R.id.fab_add_item);
        fabAddItem.setOnClickListener(v -> showAddItemPopup());


        recyclerView = view.findViewById(R.id.recyclerViewInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new InventoryAdapter(getContext(), inventoryList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        fetchDataFromFirestore();
        setupSwipeToDelete();


        return view;
    }

    private void fetchDataFromFirestore() {
        db.collection("Users")
                .document(userID)
                .collection("Inventory")
                .orderBy("lastUpdate", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore Error", error.getMessage());
                        return;
                    }

                    inventoryList.clear();

                    for (QueryDocumentSnapshot doc : value) {
                        try {
                            InventoryItem item = doc.toObject(InventoryItem.class);
                            item.setId(doc.getId()); // Set the Firestore document ID
                            inventoryList.add(item);
                        } catch (Exception e) {
                            Log.e("Mapping Error", "Error mapping document to InventoryItem", e);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                InventoryItem item = inventoryList.get(position);

                // Remove from Firestore
                db.collection("Users")
                        .document(userID)
                        .collection("Inventory")
                        .document(item.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> Log.d("Delete", "Item deleted"))
                        .addOnFailureListener(e -> Log.e("Delete Error", e.getMessage()));

                // Remove from RecyclerView
                adapter.removeItem(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                // Customize swipe effect
                float itemViewWidth = viewHolder.itemView.getWidth();
                float maxSwipeDistance = itemViewWidth * 0.3f; // 30% of the width
                dX = Math.max(dX, -maxSwipeDistance);

                // Draw red background
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                RectF background = new RectF(
                        viewHolder.itemView.getRight() + dX, // Left
                        viewHolder.itemView.getTop(), // Top
                        viewHolder.itemView.getRight(), // Right
                        viewHolder.itemView.getBottom() // Bottom
                );
                c.drawRect(background, paint);

                // Draw trash icon
                Drawable trashIcon = ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.ic_delete); // Replace with your trash icon
                int iconMargin = (viewHolder.itemView.getHeight() - trashIcon.getIntrinsicHeight()) / 2;
                int iconTop = viewHolder.itemView.getTop() + iconMargin;
                int iconBottom = iconTop + trashIcon.getIntrinsicHeight();
                int iconLeft = (int) (viewHolder.itemView.getRight() - iconMargin - trashIcon.getIntrinsicWidth());
                int iconRight = viewHolder.itemView.getRight() - iconMargin;

                trashIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                trashIcon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }



    private void showAddItemPopup() {
        Dialog dialog = new Dialog(getActivity(),R.style.DialogStyle);
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

        close.setOnClickListener(v -> {
            dialog.dismiss();
        });
        info.setOnClickListener(v -> {
            infoDialog();
        });

        // Initialize variables
        int[] quantity = {0};
        int[] lowStockAlert = {0};

        // Set default selection for spinner
        spStatus.setSelection(0);

        // Date Picker Logic
        btnPickDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(getContext(),
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
                Toast.makeText(getContext(), "Please fill all fields and set valid values", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call the function to save to Firestore
            addItemToFirestore(itemName, expireDate, measurementUnit, quantity1, lowStockAlert1);
            dialog.dismiss();

        });
    }
    private void addItemToFirestore(String itemName, String expireDate, String measurementUnit, int quantity, int lowStockAlert) {
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get current user's UID
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a map for the new inventory item
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("name", itemName);
        itemData.put("expireDate", expireDate);
        itemData.put("measurementUnit", measurementUnit);
        itemData.put("quantity", quantity);
        itemData.put("lowStockAlert", lowStockAlert);
        itemData.put("lastUpdate", Timestamp.now());

        // Add the item to the user's inventory subcollection
        db.collection("Users")
                .document(userID)
                .collection("Inventory")
                .add(itemData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Item added successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void infoDialog(){
        AlertDialog.Builder secondBuilder = new AlertDialog.Builder(getContext());
        View secondPopupView = LayoutInflater.from(getContext()).inflate(R.layout.info_popup_dialog, null);
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