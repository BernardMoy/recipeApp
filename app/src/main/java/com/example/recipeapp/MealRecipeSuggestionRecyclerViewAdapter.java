package com.example.recipeapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MealRecipeSuggestionRecyclerViewAdapter extends RecyclerView.Adapter<MealRecipeSuggestionRecyclerViewHolder> implements Filterable {

    // store a list of meal recipe preview object
    private Context ctx;
    private ArrayList<MealRecipeSuggestionPreview> previewList;
    private ArrayList<MealRecipeSuggestionPreview> previewListFull;

    // stores the recipe id that is selected. if no recipes are selected, it is -1.
    private int selectedRecipeId;

    private ImageView selectedImageView;
    private TextView selectedNameTextView;
    private TextView selectedCostTextView;


    public MealRecipeSuggestionRecyclerViewAdapter(Context ctx, ArrayList<MealRecipeSuggestionPreview> previewList){
        this.ctx = ctx;
        this.previewListFull = previewList;
        this.previewList = new ArrayList<>(previewListFull);
    }

    @NonNull
    @Override
    public MealRecipeSuggestionRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.meal_recipe_suggestion_row, parent,false);
        return new MealRecipeSuggestionRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealRecipeSuggestionRecyclerViewHolder holder, int position) {
        MealRecipeSuggestionPreview preview = previewList.get(position);

        byte[] imageByteArray = preview.getRecipeImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        holder.getRecipeImage().setImageBitmap(bitmap);

        holder.getRecipeName().setText(preview.getRecipeName());

        holder.getRecipeCost().setText(String.valueOf(preview.getRecipeCost()));

        // load the views that are shown at the selected location
        selectedImageView = ((Activity) ctx).findViewById(R.id.selectedImage_imageView);
        selectedNameTextView = ((Activity) ctx).findViewById(R.id.selectedName_textView);
        selectedCostTextView = ((Activity) ctx).findViewById(R.id.selectedCost_textView);

        holder.getSelectButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the recipe id that is clicked
                int pos = holder.getAdapterPosition();
                MealRecipeSuggestionPreview selectedRecipe = previewList.get(pos);
                selectedRecipeId = selectedRecipe.getRecipeId();  // modify global var

                // modify the selected data
                byte[] selectedImageByteArray = selectedRecipe.getRecipeImage();
                Bitmap bm = BitmapFactory.decodeByteArray(selectedImageByteArray, 0, selectedImageByteArray.length);
                selectedImageView.setImageBitmap(bm);

                selectedNameTextView.setText(selectedRecipe.getRecipeName());

                selectedCostTextView.setText(String.valueOf(selectedRecipe.getRecipeCost()));

            }
        });
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
