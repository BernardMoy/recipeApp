package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class IngredientSuggestionRecyclerViewAdapter extends RecyclerView.Adapter<IngredientSuggestionRecyclerViewHolder> implements Filterable {
    // an arraylist to store all ingredient suggestion objects
    private ArrayList<IngredientSuggestion> ingredientSuggestionList;
    private ArrayList<IngredientSuggestion> ingredientSuggestionListFull;
    private Context ctx;

    public IngredientSuggestionRecyclerViewAdapter(Context ctx, ArrayList<IngredientSuggestion> ingredientSuggestionList){
        this.ctx = ctx;
        this.ingredientSuggestionListFull = ingredientSuggestionList;
        this.ingredientSuggestionList = new ArrayList<>(ingredientSuggestionListFull);
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

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<IngredientSuggestion> filteredIngredientSuggestionList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredIngredientSuggestionList.addAll(ingredientSuggestionListFull);

            } else {
                // filter by search string
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (IngredientSuggestion i : ingredientSuggestionListFull){
                    if (i.getIngredient().toLowerCase().startsWith(filterPattern)){
                        filteredIngredientSuggestionList.add(i);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredIngredientSuggestionList;
            results.count = filteredIngredientSuggestionList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            // shoppingListPreviewList is the list to be displayed
            ingredientSuggestionList.clear();
            ingredientSuggestionList.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
