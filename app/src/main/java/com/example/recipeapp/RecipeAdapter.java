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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

    // list to store all recipe IDs that are selected through checkbox
    private HashSet<Integer> checkedRecipeIdSet;
    private HashSet<CheckBox> checkedBoxesSet;

    // a list of tags that the selected item should have
    private HashSet<String> selectedTagsSet;

    // a constraint layout for the top bar that is visible only when some items are checked
    // this bar is passed from the recipe fragment that creates the adapter
    private ConstraintLayout selectedOptionsConstraintLayout;
    private LinearLayout filterOptionsLinearLayout;

    private TextView selectedCountTextView;
    private ImageButton createShoppingListFromRecipeButton;
    private ImageButton deleteRecipesButton;
    private ImageButton deselectbutton;

    // two buttons for the dialog that pops up when delete button is clicked
    private Button cancelButton;
    private Button deleteButton;


    public RecipeAdapter(Context ctx, ArrayList<RecipePreview> recipePreviewList, ConstraintLayout selectedOptionsConstraintLayout, LinearLayout filterOptionsLinearLayout){
        this.ctx = ctx;
        this.recipePreviewListFull = recipePreviewList;
        this.recipePreviewList = new ArrayList<>(recipePreviewListFull);
        this.selectedTagsSet = new HashSet<>();
        this.favouriteFilterSelected = false;

        this.selectedOptionsConstraintLayout = selectedOptionsConstraintLayout;
        this.filterOptionsLinearLayout = filterOptionsLinearLayout;
    }

    @NonNull
    @Override
    public RecipeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.recipe_row, parent, false);
        return new RecipeRecyclerViewHolder(view);
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

        // load elements of the top bar
        selectedCountTextView = selectedOptionsConstraintLayout.findViewById(R.id.selectedCount_textView);
        createShoppingListFromRecipeButton = selectedOptionsConstraintLayout.findViewById(R.id.createShoppingListFromRecipe_button);
        deleteRecipesButton = selectedOptionsConstraintLayout.findViewById(R.id.deleteRecipes_button);
        deselectbutton = selectedOptionsConstraintLayout.findViewById(R.id.deselect_button);

        // make the top bar not visible
        toggleSelectedBar(false);

        // set up functionality of checkbox
        checkedRecipeIdSet = new HashSet<>();
        checkedBoxesSet = new HashSet<>();

        CheckBox currentCheckBox = holder.getCheckBox();
        currentCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // get the current pos and recipe id
                int pos = holder.getAdapterPosition();
                int recipeId = recipePreviewList.get(pos).getRecipeId();

                if (b) {
                    checkedRecipeIdSet.add(recipeId);
                    checkedBoxesSet.add(currentCheckBox);
                    toggleSelectedBar(true);

                } else {
                    // remove if the id is in set.
                    checkedRecipeIdSet.remove(recipeId);
                    checkedBoxesSet.remove(currentCheckBox);

                    if (checkedRecipeIdSet.isEmpty()){
                        toggleSelectedBar(false);

                    }
                }
                // update displayed count
                String countStr = String.valueOf(checkedRecipeIdSet.size()) + " selected";
                selectedCountTextView.setText(countStr);
            }
        });

        // set deselect button functionality
        deselectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectAll();
            }
        });


        deleteRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set up dialog
                Dialog dialog = new Dialog(ctx);
                dialog.setContentView(R.layout.confirm_window);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(getDrawable(ctx, R.drawable.custom_edit_text));
                dialog.setCancelable(false);

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
                        // checkedRecipeIdSet stores all recipes ids to be deleted
                        ArrayList<Integer> posToBeRemovedList = new ArrayList<>();
                        ArrayList<Integer> posToBeRemovedListFull = new ArrayList<>();

                        // add the positions to be removed to the posToBeRemovedList
                        // by checking if that row recipeId is to be deleted.
                        for (int i = 0; i < recipePreviewList.size(); i++){
                            int currentRecipeId = recipePreviewList.get(i).getRecipeId();

                            if (checkedRecipeIdSet.contains(currentRecipeId)){
                                posToBeRemovedList.add(i);
                            }
                        }

                        for (int i = 0; i < recipePreviewListFull.size(); i++){
                            int currentRecipeId = recipePreviewListFull.get(i).getRecipeId();

                            if (checkedRecipeIdSet.contains(currentRecipeId)){
                                posToBeRemovedListFull.add(i);
                            }
                        }

                        // remove item from the two arraylists in reverse order
                        for (int i = posToBeRemovedList.size() - 1; i >= 0; i--) {
                            int pos = posToBeRemovedList.get(i);
                            recipePreviewList.remove(pos);
                            notifyItemRemoved(pos);
                        }

                        for (int i = posToBeRemovedListFull.size() - 1; i >= 0; i--) {
                            int pos = posToBeRemovedListFull.get(i);
                            recipePreviewListFull.remove(pos);
                        }

                        // remove corresponding recipe ids in database
                        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);
                        for (int recipeId : checkedRecipeIdSet) {
                            db.deleteRecipeFromId(recipeId);
                        }

                        // Updates the recipe count displayed
                        String countString = String.valueOf(recipePreviewList.size()) + " results";
                        TextView textView = (TextView) ((Activity) ctx).findViewById(R.id.recipeCount_textView);
                        textView.setText(countString);

                        // Remove the dialog
                        dialog.dismiss();

                        // deselect all and return the original bars
                        deselectAll();

                    }
                });

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

    // function to turn on / off the top selected bar
    public void toggleSelectedBar(Boolean b){
        if (b) {
            selectedOptionsConstraintLayout.setVisibility(View.VISIBLE);
            selectedOptionsConstraintLayout.setEnabled(true);

            filterOptionsLinearLayout.setVisibility(View.GONE);
            filterOptionsLinearLayout.setEnabled(false);

        } else {
            selectedOptionsConstraintLayout.setVisibility(View.GONE);
            selectedOptionsConstraintLayout.setEnabled(false);

            filterOptionsLinearLayout.setVisibility(View.VISIBLE);
            filterOptionsLinearLayout.setEnabled(true);

        }
    }

    public void deselectAll(){
        // clear all checked set
        checkedRecipeIdSet.clear();
        checkedBoxesSet.clear();

        // mark all checkboxes as unvisited
        // copy is needed to prevent set change size during iter
        HashSet<CheckBox> copy = new HashSet<>(checkedBoxesSet);
        for (CheckBox cb : copy){
            cb.setChecked(false);
        }

        toggleSelectedBar(false);
    }
}
