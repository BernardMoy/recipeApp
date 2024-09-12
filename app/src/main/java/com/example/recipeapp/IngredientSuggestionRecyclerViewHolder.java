package com.example.recipeapp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientSuggestionRecyclerViewHolder extends RecyclerView.ViewHolder {

    private TextView ingredientNameTextView;
    private TextView supermarketTextView;
    private TextView costTextView;
    private TextView shelfLifeTextView;
    private ImageButton checkButton;

    public IngredientSuggestionRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        ingredientNameTextView = itemView.findViewById(R.id.ingredientName_textView);
        supermarketTextView = itemView.findViewById(R.id.supermarket_textView);
        costTextView = itemView.findViewById(R.id.cost_textView);
        shelfLifeTextView = itemView.findViewById(R.id.shelfLife_textView);
        checkButton = itemView.findViewById(R.id.ingredientCheck_button);
    }

    public TextView getIngredientNameTextView() {
        return ingredientNameTextView;
    }

    public TextView getSupermarketTextView() {
        return supermarketTextView;
    }

    public TextView getCostTextView() {
        return costTextView;
    }

    public TextView getShelfLifeTextView() {
        return shelfLifeTextView;
    }

    public ImageButton getCheckButton() {
        return checkButton;
    }
}
