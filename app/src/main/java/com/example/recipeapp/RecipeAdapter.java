package com.example.recipeapp;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeRecyclerViewHolder> implements Filterable {

    // display a list of recipes in the recipe fragment page
    private Context ctx;
    private ArrayList<RecipePreview> recipePreviewList;
    private ArrayList<RecipePreview> recipePreviewListFull;   // An arraylist that contains all original data
    private boolean favouriteFilterSelected;  // Store whether the favourite icon in the search filter is selected

    // a list of tags that the selected item should have
    private HashSet<String> selectedTagsSet;

    public RecipeAdapter(Context ctx, ArrayList<RecipePreview> recipePreviewList){
        this.ctx = ctx;
        this.recipePreviewListFull = recipePreviewList;
        this.recipePreviewList = new ArrayList<>(recipePreviewListFull);
        this.selectedTagsSet = new HashSet<>();
        this.favouriteFilterSelected = false;
    }

    @NonNull
    @Override
    public RecipeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeRecyclerViewHolder(LayoutInflater.from(ctx).inflate(R.layout.recipe_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeRecyclerViewHolder holder, int position) {

        holder.getName().setText(recipePreviewList.get(position).getName());

        String firstTag = recipePreviewList.get(position).getTag();
        if (!firstTag.isEmpty()){
            // first tag is not empty: There are tags (>=1)
            holder.getTag().setText(recipePreviewList.get(position).getTag());

            int tagPlus = recipePreviewList.get(position).getTagPlus();
            if (tagPlus != 0){
                // There is more than one tag (>1)
                String tagPlusString = " +" + String.valueOf(tagPlus);
                holder.getTagPlus().setText(tagPlusString);
            } else {
                // There is only one tag: Set tagplus to be nothing (=1)
                holder.getTagPlus().setText("");
            }
        } else {
            // There are no tags at all (=0)
            // format the first tag textview into regular text saying no tags
            holder.getTag().setText("No tags");
            holder.getTag().setTextColor(ContextCompat.getColor(ctx, R.color.gray));
            holder.getTag().setBackgroundColor(Color.TRANSPARENT);
            holder.getTag().setPadding(0,0,0,0);
            // remove the tag plus text
            holder.getTagPlus().setText("");
        }


        holder.getCost().setText(String.valueOf(recipePreviewList.get(position).getCost()));

        String prepTimeString = " " + String.valueOf(recipePreviewList.get(position).getPrepTime()) + " minutes";
        holder.getPrepTime().setText(prepTimeString);

        String timesCookedString = " " + String.valueOf(String.valueOf(recipePreviewList.get(position).getTimesCooked())) + " times cooked";
        holder.getTimesCooked().setText(timesCookedString);

        byte[] imageByteArray = recipePreviewList.get(position).getImage();

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        holder.getImage().setImageBitmap(bitmap);

        // change the displayed icon depending whether is favourited
        if (recipePreviewList.get(position).isFavourited()){
            holder.getToggleButton().setChecked(true);
        } else {
            holder.getToggleButton().setChecked(false);
        }

        // set on checked change listener for the fav toggle button
        holder.getToggleButton().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Extract the corresponding recipe id that is clicked
                int pos = holder.getAdapterPosition();
                int clickedRecipeId = recipePreviewList.get(pos).getRecipeId();
                DatabaseHelper db = new DatabaseHelper(ctx);

                if (b) {
                    // Mark the recipe as favourite from the database
                    db.updateRecipeFavourite(clickedRecipeId);
                } else {
                    // Mark the recipe as un favourited
                    db.updateRecipeUnFavourite(clickedRecipeId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipePreviewList.size();
    }

    @Override
    public Filter getFilter() {
        return recipePreviewsFilter;
    }

    private final Filter recipePreviewsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<RecipePreview> filteredRecipePreviewList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                // Only filter by tags
                for (RecipePreview recipePreview : recipePreviewListFull){
                    if (validateRecipe(recipePreview)){
                        filteredRecipePreviewList.add(recipePreview);
                    }
                }

            } else {
                // filter by both string and tags
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (RecipePreview recipePreview : recipePreviewListFull){
                    // additional filter requirements are in the validateRecipe method
                    if (recipePreview.getName().toLowerCase().contains(filterPattern) && validateRecipe(recipePreview)){
                        filteredRecipePreviewList.add(recipePreview);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredRecipePreviewList;
            results.count = filteredRecipePreviewList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            // recipePreviewList is the list to be displayed
            recipePreviewList.clear();
            recipePreviewList.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public HashSet<String> getSelectedTagList() {
        return selectedTagsSet;
    }

    public void setSelectedTagList(HashSet<String> selectedTagsSet) {
        this.selectedTagsSet = selectedTagsSet;
    }

    public boolean isFavouriteFilterSelected() {
        return favouriteFilterSelected;
    }

    public void setFavouriteFilterSelected(boolean favouriteFilterSelected) {
        this.favouriteFilterSelected = favouriteFilterSelected;
    }


    // method to validate recipe based on additional constraints.
    public boolean validateRecipe(RecipePreview recipePreview){
        // depending on whether the tag filter is checked, filter recipe by whether it is favourited
        if (favouriteFilterSelected && !recipePreview.isFavourited()){
            return false;
        }

        // validate its tags. It should contain all tags from selected tag list
        // Extract all tags from db
        DatabaseHelper db = new DatabaseHelper(ctx);
        Cursor cursor = db.getTagsFromRecipeId(recipePreview.getRecipeId());

        HashSet<String> actualTagsSet = new HashSet<>();

        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String tag = cursor.getString(0);
                actualTagsSet.add(tag);
            }
        }

        boolean tagsValidated = actualTagsSet.containsAll(selectedTagsSet);
        return tagsValidated;
    }
}
