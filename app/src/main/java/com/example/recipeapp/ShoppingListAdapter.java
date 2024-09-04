package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListRecyclerViewHolder> {

    private Context ctx;
    private ArrayList<ShoppingListPreview> shoppingListPreviewList;

    public ShoppingListAdapter(Context ctx, ArrayList<ShoppingListPreview> shoppingListPreviewList){
        this.ctx = ctx;
        this.shoppingListPreviewList = shoppingListPreviewList;
    }

    @NonNull
    @Override
    public ShoppingListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingListRecyclerViewHolder(LayoutInflater.from(ctx).inflate(R.layout.shopping_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListRecyclerViewHolder holder, int position) {

        ShoppingListPreview shoppingListPreview = shoppingListPreviewList.get(position);

        holder.getNameTextView().setText(shoppingListPreview.getName());

        String itemCountString = String.valueOf(shoppingListPreview.getItemCount()) + " items";
        holder.getItemCountTextView().setText(String.valueOf(itemCountString));

        String supermarketCountString = "(" + String.valueOf(shoppingListPreview.getSupermarketCount()) + " places)";
        holder.getSupermarketCountTextView().setText(supermarketCountString);

        holder.getCostTextView().setText(String.valueOf(shoppingListPreview.getCost()));
    }

    @Override
    public int getItemCount() {
        return shoppingListPreviewList.size();
    }
}
