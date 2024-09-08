package com.example.recipeapp;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MealRecyclerViewHolder extends RecyclerView.ViewHolder {

    private TextView categoryName;
    private ImageView recipeImage;
    private TextView recipeName;
    private TextView recipeCost;
    private ImageButton deleteButton;

    public MealRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryName = itemView.findViewById(R.id.mealRowCategory_textView);
        recipeImage = itemView.findViewById(R.id.mealRow_imageView);
        recipeName = itemView.findViewById(R.id.mealRowRecipeName_textView);
        recipeCost = itemView.findViewById(R.id.mealRowCost_textView);
        deleteButton = itemView.findViewById(R.id.deleteMeal_imageButton);
    }

    public TextView getCategoryName() {
        return categoryName;
    }

    public ImageView getRecipeImage() {
        return recipeImage;
    }

    public TextView getRecipeName() {
        return recipeName;
    }

    public TextView getRecipeCost() {
        return recipeCost;
    }

    public ImageButton getDeleteButton() {
        return deleteButton;
    }
}
