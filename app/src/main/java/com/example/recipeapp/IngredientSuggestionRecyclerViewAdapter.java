package com.example.recipeapp;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class IngredientSuggestionRecyclerViewAdapter extends RecyclerView.Adapter<IngredientSuggestionRecyclerViewHolder> implements Filterable {
    // an arraylist to store all ingredient suggestion objects
    private ArrayList<IngredientSuggestion> ingredientSuggestionList;
    private ArrayList<IngredientSuggestion> ingredientSuggestionListFull;
    private LinearLayout ingredientFieldsLinearLayout;
    private Context ctx;

    public IngredientSuggestionRecyclerViewAdapter(Context ctx, ArrayList<IngredientSuggestion> ingredientSuggestionList, LinearLayout ingredientFieldsLinearLayout){
        this.ctx = ctx;
        this.ingredientSuggestionListFull = ingredientSuggestionList;
        this.ingredientSuggestionList = new ArrayList<>(ingredientSuggestionListFull);
        this.ingredientFieldsLinearLayout = ingredientFieldsLinearLayout;
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

        String newName = ingredientSuggestion.getIngredient();
        String newSupermarket = ingredientSuggestion.getSupermarket();
        float newCost = ingredientSuggestion.getCost();
        int newShelfLife = ingredientSuggestion.getShelfLife();

        // load data
        holder.getIngredientNameTextView().setText(newName);
        holder.getSupermarketTextView().setText(newSupermarket);
        holder.getCostTextView().setText(String.valueOf(newCost));
        holder.getShelfLifeTextView().setText(String.valueOf(newShelfLife));

        // set on click listener for the check button
        holder.getCheckButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFields(newName, newSupermarket, newCost, newShelfLife);

                View currentFocus = ((Activity) ctx).getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
            }
        });
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

    // method to update the linear layout fields, when given name, supermarket, cost and shelf life
    public void updateFields(String newName, String newSupermarket, float newCost, int newShelfLife){
        TextView name = ingredientFieldsLinearLayout.findViewById(R.id.recipeIngredient_edittext);
        TextView supermarket = ingredientFieldsLinearLayout.findViewById(R.id.recipeSupermarket_autoCompleteTextView);
        TextView cost = ingredientFieldsLinearLayout.findViewById(R.id.recipeCost_edittext);
        TextView shelfLife = ingredientFieldsLinearLayout.findViewById(R.id.recipeShelfLife_edittext);

        name.setText(newName);
        supermarket.setText(newSupermarket);
        cost.setText(String.valueOf(newCost));
        shelfLife.setText(String.valueOf(newShelfLife));
    }
}
