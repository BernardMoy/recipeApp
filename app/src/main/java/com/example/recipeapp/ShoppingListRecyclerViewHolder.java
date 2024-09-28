package com.example.recipeapp;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingListRecyclerViewHolder extends RecyclerView.ViewHolder {

    private TextView nameTextView;
    private TextView itemCountTextView;
    private TextView supermarketCountTextView;
    private TextView costTextView;
    private ToggleButton favouriteButton;
    private ImageButton deleteShoppingListButton;
    private TextView statusTextView;
    private ImageButton moveToTopButton;

    public ShoppingListRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.shoppingListRow_name);
        itemCountTextView = itemView.findViewById(R.id.shoppingListRow_itemCount);
        supermarketCountTextView = itemView.findViewById(R.id.shoppingListRow_supermarketCount);
        costTextView = itemView.findViewById(R.id.shoppingListRow_cost);
        favouriteButton = itemView.findViewById(R.id.shoppingListRow_favButton);
        deleteShoppingListButton = itemView.findViewById(R.id.deleteShoppingList_button);
        statusTextView = itemView.findViewById(R.id.shoppingListRow_status);
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    public TextView getItemCountTextView() {
        return itemCountTextView;
    }

    public TextView getSupermarketCountTextView() {
        return supermarketCountTextView;
    }

    public TextView getCostTextView() {
        return costTextView;
    }

    public ToggleButton getFavouriteButton() {
        return favouriteButton;
    }

    public ImageButton getDeleteShoppingListButton() {
        return deleteShoppingListButton;
    }

    public TextView getStatusTextView() {
        return statusTextView;
    }

    public ImageButton getMoveToTopButton() {
        return moveToTopButton;
    }
}
