package com.example.recipeapp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientRecyclerViewHolder extends RecyclerView.ViewHolder {

    private TextView ingredientTextView;
    private TextView amountTextView;
    private TextView supermarketTextView;
    private TextView costTextView;
    private ImageButton deleteButton;

    public IngredientRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        ingredientTextView = itemView.findViewById(R.id.ingredient_textView);
        amountTextView = itemView.findViewById(R.id.amount_textView);
        supermarketTextView = itemView.findViewById(R.id.supermarket_textView);
        costTextView = itemView.findViewById(R.id.cost_textView);
        deleteButton = itemView.findViewById(R.id.ingredientDelete_button);
    }

    public TextView getIngredientTextView() {
        return ingredientTextView;
    }

    public TextView getAmountTextView() {
        return amountTextView;
    }

    public TextView getSupermarketTextView() {
        return supermarketTextView;
    }

    public TextView getCostTextView() {
        return costTextView;
    }

    public ImageButton getDeleteButton() {
        return deleteButton;
    }
}
