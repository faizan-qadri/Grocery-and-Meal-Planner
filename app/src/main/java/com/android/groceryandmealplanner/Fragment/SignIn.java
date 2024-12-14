package com.android.groceryandmealplanner.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.groceryandmealplanner.Data.ConnectionUtils;
import com.android.groceryandmealplanner.Activity.MainActivity;
import com.android.groceryandmealplanner.R;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends Fragment {

    private EditText email, password;
    private CardView signInBtn;
    private TextView forgotPass;
    private FirebaseAuth auth;
    public SignIn() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sign_in, container, false);

        auth = FirebaseAuth.getInstance();

        email = view.findViewById(R.id.emailSignIn);
        password = view.findViewById(R.id.passwordSignIn);
        signInBtn = view.findViewById(R.id.buttonSignIn);
        forgotPass = view.findViewById(R.id.forgotPasswordSignIn);


        //////////
        TextView textView = view.findViewById(R.id.textViewSignIn);
        String text = "Not registered yet? SignUp";
        SpannableString spannableString = new SpannableString(text);
        int startIndex = text.indexOf("SignUp");
        int endIndex = startIndex + "SignUp".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#9E8DFF")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        //////////


        forgotPass.setOnClickListener(v -> {
            String emailInput = email.getText().toString();
            if (emailInput.isEmpty()) {
                Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.sendPasswordResetEmail(emailInput)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Password reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        if (!ConnectionUtils.isNetworkAvailable(getActivity())) {
            ConnectionUtils.showConnectionErrorDialog(getActivity());
        } else {
            signInBtn.setOnClickListener(v -> {
                String emailInput = email.getText().toString();
                String passwordInput = password.getText().toString();
                if (validation(emailInput, passwordInput)) {
                    auth.signInWithEmailAndPassword(emailInput, passwordInput)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Sign In Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getContext(), "Sign In Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    validation(emailInput, passwordInput);
                }

            });
        }

        return view;
    }
    private boolean validation(String emailInput, String passwordInput) {
        boolean isValid = true;

        if (emailInput.isEmpty()) {
            email.setError("Enter Email");
            isValid = false; // Set isValid to false if email is empty
        }

        if (passwordInput.isEmpty()) {
            password.setError("Enter Password");
            isValid = false; // Set isValid to false if password is empty
        }

        return isValid; // Return true only if both fields are valid
    }

}