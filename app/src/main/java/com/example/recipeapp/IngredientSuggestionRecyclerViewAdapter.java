package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class IngredientSuggestionRecyclerViewAdapter extends RecyclerView.Adapter<IngredientSuggestionRecyclerViewHolder> {
    // an arraylist to store all ingredient suggestion objects
    private ArrayList<IngredientSuggestion> ingredientSuggestionList;
    private Context ctx;

    public IngredientSuggestionRecyclerViewAdapter(Context ctx, ArrayList<IngredientSuggestion> ingredientSuggestionList){
        this.ctx = ctx;
        this.ingredientSuggestionList = ingredientSuggestionList;
    }

    @NonNull
    @Override
    public IngredientSuggestionRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.ingredient_suggestion_row, parent,false);
        return new IngredientSuggestionRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientSuggestionRecyclerViewHolder holder, int position) {
        IngredientSuggestion ingredientSuggestion = ingredientSuggestionList.get(position);

        // load data
        holder.getIngredientNameTextView().setText(ingredientSuggestion.getIngredient());
        holder.getSupermarketTextView().setText(ingredientSuggestion.getSupermarket());
        holder.getCostTextView().setText(String.valueOf(ingredientSuggestion.getCost()));
        holder.getShelfLifeTextView().setText(String.valueOf(ingredientSuggestion.getShelfLife()));
    }

    @Override
    public int getItemCount() {
        return ingredientSuggestionList.size();
    }
}
