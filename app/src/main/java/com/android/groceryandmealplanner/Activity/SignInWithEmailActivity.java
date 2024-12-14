package com.android.groceryandmealplanner.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.groceryandmealplanner.Fragment.SignIn;
import com.android.groceryandmealplanner.Fragment.SignUp;
import com.android.groceryandmealplanner.R;


public class SignInWithEmailActivity extends AppCompatActivity {

    CardView signIn, signUp;
    TextView signInTxt, signUpTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_with_email);

        signIn = findViewById(R.id.signInButton);
        signUp = findViewById(R.id.signUpButton);
        signInTxt = findViewById(R.id.signInButtonTxt);
        signUpTxt = findViewById(R.id.signUpButtonTxt);
        loadFragment1(new SignIn());

        signIn.setOnClickListener(v -> {
            signIn.setCardBackgroundColor(Color.parseColor("#9E8DFF"));
            signInTxt.setTextColor(Color.parseColor("#FFFFFF"));
            signUp.setCardBackgroundColor(Color.parseColor("#D9D9D9"));
            signUpTxt.setTextColor(Color.parseColor("#000000"));
            loadFragment1(new SignIn());

        });

        signUp.setOnClickListener(v -> {
            signUp.setCardBackgroundColor(Color.parseColor("#9E8DFF"));
            signUpTxt.setTextColor(Color.parseColor("#FFFFFF"));
            signIn.setCardBackgroundColor(Color.parseColor("#D9D9D9"));
            signInTxt.setTextColor(Color.parseColor("#000000"));
            loadFragment1(new SignUp());

        });


    }

    private void loadFragment1(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.signIn_Up_Frame, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}