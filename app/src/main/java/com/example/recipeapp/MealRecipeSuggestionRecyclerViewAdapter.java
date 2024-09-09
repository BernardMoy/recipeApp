package com.example.recipeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MealRecipeSuggestionRecyclerViewAdapter extends RecyclerView.Adapter<MealRecipeSuggestionRecyclerViewHolder> implements Filterable {

    // store a list of meal recipe preview object
    private Context ctx;
    private ArrayList<MealRecipeSuggestionPreview> previewList;
    private ArrayList<MealRecipeSuggestionPreview> previewListFull;


    public MealRecipeSuggestionRecyclerViewAdapter(Context ctx, ArrayList<MealRecipeSuggestionPreview> previewList){
        this.ctx = ctx;
        this.previewListFull = previewList;
        this.previewList = new ArrayList<>(previewListFull);
    }

    @NonNull
    @Override
    public MealRecipeSuggestionRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MealRecipeSuggestionRecyclerViewHolder(LayoutInflater.from(ctx).inflate(R.layout.meal_recipe_suggestion_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MealRecipeSuggestionRecyclerViewHolder holder, int position) {
        MealRecipeSuggestionPreview preview = previewList.get(position);

        byte[] imageByteArray = preview.getRecipeImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        holder.getRecipeImage().setImageBitmap(bitmap);

        holder.getRecipeName().setText(preview.getRecipeName());

        holder.getRecipeCost().setText(String.valueOf(preview.getRecipeCost()));
    }

    @Override
    public int getItemCount() {
        return previewList.size();
    }

    @Override
    public Filter getFilter() {
        return mealRecipeFilter;
    }

    private final Filter mealRecipeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<MealRecipeSuggestionPreview> filteredPreviewList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredPreviewList.addAll(previewListFull);

            } else {
                // filter by search string
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (MealRecipeSuggestionPreview preview : previewListFull){
                    if (preview.getRecipeName().toLowerCase().contains(filterPattern)){
                        filteredPreviewList.add(preview);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredPreviewList;
            results.count = filteredPreviewList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            // shoppingListPreviewList is the list to be displayed
            previewList.clear();
            previewList.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
