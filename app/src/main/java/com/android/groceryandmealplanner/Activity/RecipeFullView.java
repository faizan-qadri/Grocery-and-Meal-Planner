package com.android.groceryandmealplanner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.groceryandmealplanner.Data.Recipe;
import com.android.groceryandmealplanner.R;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;


public class RecipeFullView extends AppCompatActivity {

    ImageView backpressedBtn, mainImage;
    TextView title, ingredients, stepsToMake, tips, timeToMake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_full_view);

        title = findViewById(R.id.recipeFV_Title);
        mainImage = findViewById(R.id.recipeFV_mainImg);
        ingredients = findViewById(R.id.recipeFV_Ingredients);
        stepsToMake = findViewById(R.id.recipeFV_StepsToMake);
        tips = findViewById(R.id.recipeFV_Tips);
        timeToMake = findViewById(R.id.recipeFV_TimeToMake);

        backpressedBtn = findViewById(R.id.recipeFV_backPressed);
        backpressedBtn.setOnClickListener(v -> onBackPressed());


        // Retrieve the Recipe object
        Recipe recipe = getIntent().getParcelableExtra("recipe");

        if (recipe != null) {
            title.setText(recipe.getName());
            timeToMake.setText(recipe.getTimeToMake());
            Glide.with(this)
                    .load(recipe.getUrl())
                    .placeholder(R.drawable.grocery) // Default placeholder while loading
                    .into(mainImage);
            /////Sets To Make
            List<String> steps = recipe.getSteps(); // Get the list of steps
            StringBuilder stepsText = new StringBuilder();
            int stepNumber = 1; // Start from 1
            for (String step : steps) {
                stepsText.append(stepNumber).append(". ").append(step).append(" \n"); // Add the number, step text, and newline
                stepNumber++; // Increment the step number for the next step
            }
            stepsToMake.setText(stepsText.toString());
            ////////////Tips
            List<String> tips1 = recipe.getTips(); // Get the list of steps
            StringBuilder tipsText = new StringBuilder();
            int tipsNumber = 1; // Start from 1
            for (String step : tips1) {
                tipsText.append(tipsNumber).append(". ").append(step).append("\n"); // Add each step, separated by a newline
                tipsNumber++;
            }
            tips.setText(tipsText.toString());
            ////////////////Ingredients
            StringBuilder ingredientsText = new StringBuilder();
        for (Map<String, String> ingredient : recipe.getIngredients()) {
            ingredientsText.append("• ")
                    .append(ingredient.get("name"))
                    .append(" (")
                    .append(ingredient.get("type"))
                    .append("): ")
                    .append(ingredient.get("quantity"))
                    .append("\n");
            }
            ingredients.setText(ingredientsText.toString());

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}