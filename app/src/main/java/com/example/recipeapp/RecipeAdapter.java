package com.example.recipeapp;

import static androidx.core.content.ContextCompat.getDrawable;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeRecyclerViewHolder> implements Filterable {

    // display a list of recipes in the recipe fragment page
    private Context ctx;
    private ArrayList<RecipePreview> recipePreviewList;
    private ArrayList<RecipePreview> recipePreviewListFull;   // An arraylist that contains all original data
    private boolean favouriteFilterSelected;  // Store whether the favourite icon in the search filter is selected

    // a list of tags that the selected item should have
    private HashSet<String> selectedTagsSet;

    // two buttons for the dialog that pops up when delete button is clicked
    private Button cancelButton;
    private Button deleteButton;


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

        // Set variables
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

        // discard the prev listener
        holder.getToggleButton().setOnCheckedChangeListener(null);

        // change the displayed icon depending whether is favourited
        RecipePreview currentRecipe = recipePreviewList.get(position);

        holder.getToggleButton().setChecked(currentRecipe.isFavourited());

        // set on checked change listener for the fav toggle button
        holder.getToggleButton().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Extract the corresponding recipe id that is clicked
                int pos = holder.getAdapterPosition();

                // must use recipePreviewList instead of full. Operation is done on filtered results.
                int clickedRecipeId = recipePreviewList.get(pos).getRecipeId();
                DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);

                if (b) {
                    // Mark the recipe as favourite from the database
                    db.updateRecipeFavourite(clickedRecipeId);
                    recipePreviewList.get(pos).setIsFavourited(true);

                } else {
                    // Mark the recipe as un favourited
                    db.updateRecipeUnFavourite(clickedRecipeId);
                    recipePreviewList.get(pos).setIsFavourited(false);
                }
            }
        });

        // set up listener for the delete button of a recipe
        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();

                Dialog dialog = new Dialog(ctx);
                dialog.setContentView(R.layout.confirm_window);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(getDrawable(ctx, R.drawable.custom_edit_text));
                dialog.setCancelable(false);     // wont disappear when clicked outside of it

                // load the two buttons
                cancelButton = dialog.findViewById(R.id.confirmRecipeCancel_button);
                deleteButton = dialog.findViewById(R.id.confirmRecipeDelete_button);

                // Set on click listeners for the two buttons
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Remove the dialog
                        dialog.dismiss();
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Delete the recipe here
                        int recipeId = recipePreviewList.get(pos).getRecipeId();

                        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);
                        db.deleteRecipeFromId(recipeId);

                        // update the recipe Preview lists
                        for (int i = 0; i < recipePreviewList.size(); i++){
                            if (recipePreviewList.get(i).getRecipeId() == recipeId){
                                recipePreviewList.remove(i);
                                break;
                            }
                        }

                        for (int i = 0; i < recipePreviewListFull.size(); i++){
                            if (recipePreviewListFull.get(i).getRecipeId() == recipeId){
                                recipePreviewListFull.remove(i);
                                break;
                            }
                        }

                        // Updates the recipe count displayed
                        String countString = String.valueOf(recipePreviewList.size()) + " results";
                        TextView textView = (TextView) ((Activity) ctx).findViewById(R.id.recipeCount_textView);
                        textView.setText(countString);

                        // update the recyclerview
                        notifyItemRemoved(pos);

                        // Remove the dialog
                        dialog.dismiss();
                    }
                });

                // show dialog
                dialog.show();
            }
        });


        // Set up listener for the entire recyclerview: When it is clicked, addNewRecipe activity is launched
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Extract the corresponding recipe id that is clicked
                int pos = holder.getAdapterPosition();
                int clickedRecipeId = recipePreviewList.get(pos).getRecipeId();

                // Start intent with passed parameters of recipe
                Intent i = new Intent(ctx, AddNewRecipe.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                // pass the recipe id to the edit recipe activity.
                // Information is extracted from db there
                i.putExtra("recipe_id", clickedRecipeId);
                i.putExtra("title_text", "Edit recipe");

                ctx.startActivity(i);
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
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);
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
