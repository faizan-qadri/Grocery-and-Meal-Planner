package com.android.groceryandmealplanner.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.groceryandmealplanner.Activity.SignInWithEmailActivity;
import com.android.groceryandmealplanner.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInBottomSheetFragment extends BottomSheetDialogFragment {


    private LinearLayout buttonEmailSignIn;
    private LinearLayout buttonGoogleSignIn;

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInFragment";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    public SignInBottomSheetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sign_in_bottom_sheet, container, false);

        buttonEmailSignIn = view.findViewById(R.id.buttonEmailSignIn);
        buttonGoogleSignIn = view.findViewById(R.id.buttonGoogleSignIn);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        buttonEmailSignIn.setOnClickListener(v -> emailSignIn());
        buttonGoogleSignIn.setOnClickListener(v -> googleSignIn());


        return view;
    }

    private void emailSignIn() {
        // Implement email sign-in logic here
        Intent intent = new Intent(getActivity(), SignInWithEmailActivity.class);
        startActivity(intent);
    }

    private void googleSignIn() {
        // Implement Google sign-in logic here
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                // Get Google Sign-In account
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);

                // Google Sign-In succeeded
                Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign-In failed
                Log.w(TAG, "Google sign-in failed", e);
                Toast.makeText(requireContext(), "Google Sign-In failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign-In success
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "signInWithCredential:success - User: " + user.getDisplayName());
                        Toast.makeText(requireContext(), "Welcome, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Sign-In failed
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(requireContext(), "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}