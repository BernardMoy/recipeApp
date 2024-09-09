package com.example.recipeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MealRecipeSuggestionAdapter extends ArrayAdapter<MealRecipeSuggestionPreview> {

    // store objects
    private ArrayList<MealRecipeSuggestionPreview> previewList;
    private ArrayList<MealRecipeSuggestionPreview> previewListFull;

    public MealRecipeSuggestionAdapter(@NonNull Context context, ArrayList<MealRecipeSuggestionPreview> previewList) {
        super(context, 0, previewList);

        this.previewListFull = previewList;
        this.previewList = new ArrayList<>(previewListFull);
    }

    /*
    Return the view objects where they are in the rows to be modified.
     */
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meal_recipe_suggestion_row, parent, false);
        }

        // get fields from the row layout
        ImageView recipeImage = convertView.findViewById(R.id.mealRecipeSuggestion_imageView);
        TextView recipeName = convertView.findViewById(R.id.mealRecipeSuggestionRecipeName_textView);
        TextView recipeCost = convertView.findViewById(R.id.mealRecipeSuggestionCost_textView);

        // set fields from the getItem() method
        MealRecipeSuggestionPreview mealRecipeSuggestionPreview = getItem(position);

        byte[] imageByteArray = mealRecipeSuggestionPreview.getRecipeImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        recipeImage.setImageBitmap(bitmap);

        recipeName.setText(mealRecipeSuggestionPreview.getRecipeName());

        recipeCost.setText(String.valueOf(mealRecipeSuggestionPreview.getRecipeCost()));



        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter(){
        return mealRecipeSuggestionFilter;
    }

    // define custom filter
    private Filter mealRecipeSuggestionFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<MealRecipeSuggestionPreview> filteredPreviewList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0){
                filteredPreviewList.addAll(previewListFull);

            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                // filter from the full list
                for (MealRecipeSuggestionPreview m : previewListFull){
                    if (m.getRecipeName().toLowerCase().contains(filterPattern)){
                        filteredPreviewList.add(m);
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
            previewList.clear();
            previewList.addAll((ArrayList) filterResults.values);  // previewList stores all filtered results
            notifyDataSetChanged();
        }
    };
}
