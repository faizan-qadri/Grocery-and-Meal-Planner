package com.android.groceryandmealplanner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.groceryandmealplanner.Fragment.Home;
import com.android.groceryandmealplanner.Fragment.Inventory;
import com.android.groceryandmealplanner.Fragment.Meal;
import com.android.groceryandmealplanner.Fragment.Notification;
import com.android.groceryandmealplanner.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView userName;
    private ImageView notificationBtn, popupBtn;
    private TextView textView2;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        firebaseAuth = FirebaseAuth.getInstance();

        userName = findViewById(R.id.user_name);
        notificationBtn = findViewById(R.id.notification_btn);
        popupBtn = findViewById(R.id.popupBtnMain);

        popupBtn.setOnClickListener(v -> logOutPopup());

        db = FirebaseFirestore.getInstance();

        // Reference to the "users" collection
        CollectionReference usersCollection = db.collection("Users");

        // Retrieve user data
        fetchUserData(usersCollection);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadFragmentMainActivity(new Inventory());

        // Set listener for navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            notificationBtn.setColorFilter(ContextCompat.getColor(this, R.color.gray), PorterDuff.Mode.SRC_IN);
            if (item.getItemId() == R.id.nav_home) {
                loadFragmentMainActivity(new Home());

            }else if (item.getItemId() == R.id.nav_inventory){
                loadFragmentMainActivity(new Inventory());

            }else if (item.getItemId() == R.id.nav_meal){
                loadFragmentMainActivity(new Meal());

            }else {
                loadFragmentMainActivity(new Home());

            }
            return false;
        });

        notificationBtn.setOnClickListener(v -> {
            notificationBtn.setColorFilter(ContextCompat.getColor(this, R.color.purple), PorterDuff.Mode.SRC_IN);


            loadFragmentMainActivity(new Notification());

            bottomNavigationView.getMenu().setGroupCheckable(0, true, false);
            for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
                bottomNavigationView.getMenu().getItem(i).setChecked(false);
            }
        });

    }

    private void logOutPopup() {
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.popup_window_layout);

        dialog.setTitle("Are You Sure you want to Log Out?");

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnClose = dialog.findViewById(R.id.LogoutBtn);
        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
            signOut();
        });

        dialog.show();
    }

    private void signOut() {
        try {
            // Sign out using FirebaseAuth
            firebaseAuth.signOut();

            // Navigate back to the login screen
            Intent intent = new Intent(MainActivity.this, Splash_Screen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Close the current activity
            Toast.makeText(MainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserData(CollectionReference usersCollection) {
        // Example of getting all documents
        usersCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            userName.setText("Hi "+name+",");
                        }
                    } else {
                        Log.e("FirestoreError", "Error getting documents", task.getException());
                    }
                });
    }
    public void loadFragmentMainActivity(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }
}