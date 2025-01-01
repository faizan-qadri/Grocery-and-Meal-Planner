package com.android.groceryandmealplanner.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.groceryandmealplanner.Data.Recipe;
import com.android.groceryandmealplanner.Data.RecipeAdapter;
import com.android.groceryandmealplanner.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Meal extends Fragment {

    private RecyclerView recipeRecyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal, container, false);

        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchRecipesFromFirestore();


        return view;
    }
    private void fetchRecipesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Recipes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Recipe recipe = new Recipe();

                    // Fetch basic details
                    recipe.setName(document.getString("name"));
                    recipe.setUrl(document.getString("url"));
                    recipe.setTimeToMake(document.getString("timeToMake"));
                    recipe.setSteps((List<String>) document.get("steps"));
                    recipe.setTips((List<String>) document.get("tips"));

                    // Fetch ingredients as an array of maps
                    List<Map<String, String>> ingredients = (List<Map<String, String>>) document.get("ingredients");
                    if (ingredients != null) {
                        recipe.setIngredients(ingredients);
                    }
                    // Add the recipe to the list
                    recipeList.add(recipe);
                }

                // Set up the adapter
                recipeAdapter = new RecipeAdapter(recipeList);
                recipeRecyclerView.setAdapter(recipeAdapter);
            } else {
                Log.e("Firestore", "Error getting documents: ", task.getException());
            }

    });
    }

}