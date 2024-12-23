package com.android.groceryandmealplanner.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.groceryandmealplanner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


public class Inventory extends Fragment {

    private FirebaseFirestore db;
    private String userID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        db = FirebaseFirestore.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID

        CardView fabAddItem = view.findViewById(R.id.fab_add_item);
        fabAddItem.setOnClickListener(v -> showAddItemPopup());

        return view;
    }

    private void showAddItemPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_add_item, null);

        EditText etItemName = popupView.findViewById(R.id.et_item_name);
        EditText etExpireDate = popupView.findViewById(R.id.et_expire_date);
        Spinner spStatus = popupView.findViewById(R.id.sp_status);
        TextView tvQuantity = popupView.findViewById(R.id.tv_quantity);
        TextView tvLowStockAlert = popupView.findViewById(R.id.tv_low_stock_alert);
        Button btnAddItem = popupView.findViewById(R.id.btn_add_item);

        Button btnIncreaseQuantity = popupView.findViewById(R.id.btn_increase_quantity);
        Button btnDecreaseQuantity = popupView.findViewById(R.id.btn_decrease_quantity);
        Button btnIncreaseAlert = popupView.findViewById(R.id.btn_increase_alert);
        Button btnDecreaseAlert = popupView.findViewById(R.id.btn_decrease_alert);
        ImageButton btnPickDate = popupView.findViewById(R.id.btn_pick_date);

        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.show();

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
        btnIncreaseQuantity.setOnClickListener(v -> adjustQuantity(spStatus, quantity, tvQuantity, true));
        btnDecreaseQuantity.setOnClickListener(v -> adjustQuantity(spStatus, quantity, tvQuantity, false));

        // Low Stock Alert Logic
        btnIncreaseAlert.setOnClickListener(v -> adjustAlert(lowStockAlert, quantity[0], tvLowStockAlert, true));
        btnDecreaseAlert.setOnClickListener(v -> adjustAlert(lowStockAlert, quantity[0], tvLowStockAlert, false));

        // Add Item Button Logic
        btnAddItem.setOnClickListener(v -> {
            String itemName = etItemName.getText().toString().trim();
            String expireDate = etExpireDate.getText().toString().trim();
            String status = spStatus.getSelectedItem().toString();

            if (itemName.isEmpty() || expireDate.isEmpty() || quantity[0] == 0) {
                Toast.makeText(getContext(), "Please fill all fields and set valid values", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firestore integration here...
        });
    }

    private void adjustQuantity(Spinner spStatus, int[] quantity, TextView tvQuantity, boolean increase) {
        String status = spStatus.getSelectedItem().toString();
        int step = 1; // Default step for Kilogram

        switch (status) {
            case "Gram":
                step = 100;
                break;
            case "Milliliter":
                step = 10;
                break;
            case "Piece":
            case "Dozen":
                step = 1;
                break;
        }

        if (increase) {
            quantity[0] += step;
        } else if (quantity[0] > 0) {
            quantity[0] = Math.max(quantity[0] - step, 0);
        }

        tvQuantity.setText(String.valueOf(quantity[0]));
    }

    private void adjustAlert(int[] lowStockAlert, int maxQuantity, TextView tvLowStockAlert, boolean increase) {
        if (increase) {
            if (lowStockAlert[0] < maxQuantity) {
                lowStockAlert[0]++;
            }
        } else if (lowStockAlert[0] > 0) {
            lowStockAlert[0]--;
        }
        tvLowStockAlert.setText(String.valueOf(lowStockAlert[0]));
    }

}