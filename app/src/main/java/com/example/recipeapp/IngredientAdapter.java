package com.example.recipeapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientRecyclerViewHolder> {

    // display a row of ingredient, portionSize, supermarket and cost
    private Context ctx;
    private List<Ingredient> ingredientList;

    public IngredientAdapter(Context ctx, List<Ingredient> ingredientList){
        this.ctx = ctx;
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public IngredientRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientRecyclerViewHolder(LayoutInflater.from(ctx).inflate(R.layout.ingredient_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientRecyclerViewHolder holder, int position) {
        // set text for the item row
        holder.getIngredientTextView().setText(ingredientList.get(position).getIngredient());
        holder.getAmountTextView().setText(String.valueOf(ingredientList.get(position).getAmount()));
        holder.getSupermarketTextView().setText(ingredientList.get(position).getSupermarket());
        holder.getCostTextView().setText(String.valueOf(ingredientList.get(position).getCost()));

        // set alternating row colours
        if (position % 2 == 0){
            holder.getIngredientTextView().setBackgroundResource(R.color.lightColor);
            holder.getAmountTextView().setBackgroundResource(R.color.lightColor);
            holder.getSupermarketTextView().setBackgroundResource(R.color.lightColor);
            holder.getCostTextView().setBackgroundResource(R.color.lightColor);
        }

    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }
}
