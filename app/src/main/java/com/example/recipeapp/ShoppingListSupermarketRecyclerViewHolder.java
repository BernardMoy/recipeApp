package com.example.recipeapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingListSupermarketRecyclerViewHolder extends RecyclerView.ViewHolder{

    // elements that are contained in this recyclerview
    private TextView supermarketNameTextView;
    private TextView supermarketCountTextView;
    private TextView supermarketTotalCostTextView;
    private RecyclerView shoppingListIngredientsRecyclerView;

    public ShoppingListSupermarketRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        supermarketNameTextView = itemView.findViewById(R.id.supermarketName_textView);
        supermarketCountTextView = itemView.findViewById(R.id.supermarketCount_textView);
        supermarketTotalCostTextView = itemView.findViewById(R.id.supermarketTotalCost_textView);
        shoppingListIngredientsRecyclerView = itemView.findViewById(R.id.shoppingListIngredients_recyclerView);
    }

    public TextView getSupermarketNameTextView() {
        return supermarketNameTextView;
    }

    public TextView getSupermarketCountTextView() {
        return supermarketCountTextView;
    }

    public TextView getSupermarketTotalCostTextView() {
        return supermarketTotalCostTextView;
    }

    public RecyclerView getShoppingListIngredientsRecyclerView() {
        return shoppingListIngredientsRecyclerView;
    }
}
