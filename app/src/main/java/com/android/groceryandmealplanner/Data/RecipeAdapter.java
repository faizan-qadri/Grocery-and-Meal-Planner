package com.android.groceryandmealplanner.Data;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.groceryandmealplanner.Activity.RecipeFullView;
import com.android.groceryandmealplanner.R;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;

    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.recipeName.setText(recipe.getName()+" \uD83D\uDE0B");
        holder.recipeTime.setText("Time: " + recipe.getTimeToMake());
        Glide.with(holder.recipeImage.getContext())
                .load(recipe.getUrl())
                .placeholder(R.drawable.grocery) // Default placeholder while loading
                .into(holder.recipeImage);


        // Display ingredients
        StringBuilder ingredientsText = new StringBuilder();
        for (Map<String, String> ingredient : recipe.getIngredients()) {
            ingredientsText
                    .append(ingredient.get("name"))
                    .append(" | ");
        }
        holder.recipeIngredent.setText(ingredientsText.toString());


        holder.fullViewRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.recipeImage.getContext(), RecipeFullView.class);

                // Pass the selected Recipe object
                intent.putExtra("recipe", recipeList.get(holder.getAdapterPosition()));

                // Start the target activity
                holder.recipeImage.getContext().startActivity(intent);
            }
        });



    }


    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName, recipeTime, recipeIngredent;
        CardView fullViewRecipeBtn;

        ImageView recipeImage;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipeTime = itemView.findViewById(R.id.recipeTime);
            recipeIngredent = itemView.findViewById(R.id.recipeIngredent);
            fullViewRecipeBtn = itemView.findViewById(R.id.recipeBtn);
            recipeImage = itemView.findViewById(R.id.recipeImage);

        }
    }
}
