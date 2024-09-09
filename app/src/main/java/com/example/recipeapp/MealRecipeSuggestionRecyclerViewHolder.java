package com.example.recipeapp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class MealRecipeSuggestionRecyclerViewHolder extends RecyclerView.ViewHolder {

    private ImageView recipeImage;
    private TextView recipeName;
    private TextView recipeCost;
    private Button selectButton;

    public MealRecipeSuggestionRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        recipeImage = itemView.findViewById(R.id.mealRecipeSuggestion_imageView);
        recipeName = itemView.findViewById(R.id.mealRecipeSuggestionRecipeName_textView);
        recipeCost = itemView.findViewById(R.id.mealRecipeSuggestionCost_textView);
        selectButton = itemView.findViewById(R.id.select_button);
    }

    public ImageView getRecipeImage() {
        return recipeImage;
    }

    public TextView getRecipeName() {
        return recipeName;
    }

    public Button getSelectButton() {
        return selectButton;
    }

    public TextView getRecipeCost() {
        return recipeCost;
    }
}
