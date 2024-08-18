package com.example.recipeapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientRecyclerViewHolder extends RecyclerView.ViewHolder {

    private TextView ingredientTextView;
    private TextView portionSizeTextView;
    private TextView supermarketTextView;
    private TextView costTextView;

    public IngredientRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        ingredientTextView = itemView.findViewById(R.id.ingredient_textView);
        portionSizeTextView = itemView.findViewById(R.id.portionSize_textView);
        supermarketTextView = itemView.findViewById(R.id.supermarket_textView);
        costTextView = itemView.findViewById(R.id.cost_textView);
    }

    public TextView getIngredientTextView() {
        return ingredientTextView;
    }

    public TextView getPortionSizeTextView() {
        return portionSizeTextView;
    }

    public TextView getSupermarketTextView() {
        return supermarketTextView;
    }

    public TextView getCostTextView() {
        return costTextView;
    }
}
