package com.android.groceryandmealplanner.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.groceryandmealplanner.Data.ConnectionUtils;
import com.android.groceryandmealplanner.Activity.MainActivity;
import com.android.groceryandmealplanner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UserAuthentication_SplashScreen extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView btn;


    public UserAuthentication_SplashScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_authentication, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();

        btn = view.findViewById(R.id.btnBottomSheet);
        btn.setVisibility(View.INVISIBLE);

        bottomSheet();

        btn.setOnClickListener(v -> bottomSheet());

        return view;

    }

    private void bottomSheet(){
        if (!ConnectionUtils.isNetworkAvailable(getContext())) {
            ConnectionUtils.showConnectionErrorDialog(getContext());
        } else {
            //if connection is available
            if (firebaseUser == null){
                btn.setVisibility(View.VISIBLE);
                SignInBottomSheetFragment bottomSheetFragment = new SignInBottomSheetFragment();
                bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());

            }else {
                // Timer Thread for Splash Screen
                Thread splashThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            // Sleep for 3 seconds (3000 milliseconds)
                            sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            // Navigate to MainActivity
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                };

                // Start the thread
                splashThread.start();
            }
        }
    }
}