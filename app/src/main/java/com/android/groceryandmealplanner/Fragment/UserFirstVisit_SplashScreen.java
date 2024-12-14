package com.android.groceryandmealplanner.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.groceryandmealplanner.Activity.Splash_Screen;
import com.android.groceryandmealplanner.R;


public class UserFirstVisit_SplashScreen extends Fragment {

    private CardView getStart;

    public UserFirstVisit_SplashScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_first_visit, container, false);

        getStart = view.findViewById(R.id.new_user_get_start_btn);

        getStart.setOnClickListener(v -> {
            if (getActivity() != null) {
                // Using the activity's loadFragment method to transition to Fragment2
                ((Splash_Screen) getActivity()).loadFragment(new UserAuthentication_SplashScreen());
            }
        });
        return view;
    }
}