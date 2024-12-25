package com.android.groceryandmealplanner.Fragment;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUp extends Fragment {

    private EditText email, password , name;
    private CardView signUpBtn;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        email = view.findViewById(R.id.emailSignUp);
        password = view.findViewById(R.id.passwordSignUp);
        signUpBtn = view.findViewById(R.id.buttonSignUp);
        name = view.findViewById(R.id.nameSignUp);


        //////////
        TextView textView = view.findViewById(R.id.textViewSignUp);
        String text = "Already registered? SignIn";
        SpannableString spannableString = new SpannableString(text);
        int startIndex = text.indexOf("SignIn");
        int endIndex = startIndex + "SignIn".length();
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#9E8DFF")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        //////////

        if (!ConnectionUtils.isNetworkAvailable(getActivity())) {
            ConnectionUtils.showConnectionErrorDialog(getActivity());
        } else {
            signUpBtn.setOnClickListener(v -> {
                String emailInput = email.getText().toString();
                String passwordInput = password.getText().toString();
                String nameStr = name.getText().toString();
                if (validation(nameStr, emailInput, passwordInput)) {
                    auth.createUserWithEmailAndPassword(emailInput, passwordInput)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                    /////Firestore for storing Data
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("name", nameStr);
                                    user.put("email", emailInput);
                                    user.put("userID", auth.getCurrentUser().getUid());
                                    user.put("User Credential", "Email");
                                    db.collection("Users")
                                            .document(auth.getCurrentUser().getUid())
                                            .set(user)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d(TAG, "DocumentSnapshot added with ID: " + auth.getCurrentUser().getUid());
                                                Toast.makeText(getActivity(), "Data stored successfully!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.w(TAG, "Error adding document", e);
                                                Toast.makeText(getActivity(), "Error storing data.", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(getContext(), "Sign Up Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    validation(nameStr,emailInput, passwordInput);
                }
            });

        }

        return view;
    }

    private boolean validation(String nameStr, String emailInput, String passwordInput) {
        boolean isValid = true;

        if (nameStr.isEmpty()) {
            name.setError("Enter Name");
            isValid = false; // Set isValid to false if email is empty
        }

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