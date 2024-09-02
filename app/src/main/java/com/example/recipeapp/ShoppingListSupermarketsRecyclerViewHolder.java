package com.example.recipeapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingListSupermarketsRecyclerViewHolder extends RecyclerView.ViewHolder {

    // elements that are contained in this recyclerview
    private TextView supermarketNameTextView;
    private TextView supermarketCountTextView;
    private TextView supermarketTotalCostTextView;
    private RecyclerView shoppingListIngredientsRecyclerView;

    public ShoppingListSupermarketsRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
