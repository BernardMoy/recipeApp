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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import kotlin.Triple;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeRecyclerViewHolder> implements Filterable {

    // display a list of recipes in the recipe fragment page
    private Context ctx;
    private ArrayList<RecipePreview> recipePreviewList;
    private ArrayList<RecipePreview> recipePreviewListFull;   // An arraylist that contains all original data
    private boolean favouriteFilterSelected;  // Store whether the favourite icon in the search filter is selected


    /*
    list to store all recipe IDs that are selected through checkbox
    Recipe selection data is ONLY stored in this hashmap, so when the recipes get updated, the hashmap resets.
     */
    private HashMap<Integer, Integer> selectedRecipeIdMap;

    // a list of tags that the selected item should have
    private HashSet<String> selectedTagsSet;

    // a constraint layout for the top bar that is visible only when some items are checked
    // this bar is passed from the recipe fragment that creates the adapter
    private ConstraintLayout selectedOptionsConstraintLayout;

    private TextView selectedCountTextView;
    private ImageButton createShoppingListFromRecipeButton;
    private ImageButton deleteRecipesButton;
    private ImageButton deselectbutton;

    // two buttons for the dialog that pops up when delete button is clicked
    private Button cancelButton;
    private Button deleteButton;

    // two buttons for the dialog that pops up to create SL from recipes
    private Button cancelButton2;
    private Button createShoppingListButton;
    private EditText shoppingListNameEditText;


    public RecipeAdapter(Context ctx, ArrayList<RecipePreview> recipePreviewList, ConstraintLayout selectedOptionsConstraintLayout, LinearLayout filterOptionsLinearLayout){
        this.ctx = ctx;
        this.recipePreviewListFull = recipePreviewList;
        this.recipePreviewList = new ArrayList<>(recipePreviewListFull);
        this.selectedTagsSet = new HashSet<>();
        this.favouriteFilterSelected = false;
        this.selectedOptionsConstraintLayout = selectedOptionsConstraintLayout;
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
            holder.getTag().setTextColor(ContextCompat.getColor(ctx, R.color.white));
            holder.getTag().setBackgroundColor(ContextCompat.getColor(ctx, R.color.primaryColor));

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
            // remove the tag plus text
            holder.getTagPlus().setText("");
        }


        holder.getCost().setText(String.valueOf(recipePreviewList.get(position).getCost()));

        String prepTimeString = " " + String.valueOf(recipePreviewList.get(position).getPrepTime()) + " minutes";
        holder.getPrepTime().setText(prepTimeString);

        String timesCookedString = " " + String.valueOf(String.valueOf(recipePreviewList.get(position).getTimesCooked())) + " cooked";
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
                db.close();
            }
        });





        // load elements of the top bar
        selectedCountTextView = selectedOptionsConstraintLayout.findViewById(R.id.selectedCount_textView);
        createShoppingListFromRecipeButton = selectedOptionsConstraintLayout.findViewById(R.id.createShoppingListFromRecipe_button);
        deselectbutton = selectedOptionsConstraintLayout.findViewById(R.id.deselect_button);

        // make the top bar not visible -- this is done BY DEFAULT
        toggleSelectedBar(false);

        // set up functionality of add buttons -- the hashmap is reset BY DEFAULT
        selectedRecipeIdMap = new HashMap<>();

        ImageButton currentAddButton = holder.getAddImageButton();
        ImageButton currentMinusButton = holder.getMinusImageButton();
        TextView currentCountTextView = holder.getCountTextView();

        // modify the textview to show "0"
        currentCountTextView.setText("0");

        // add the recipe data to the hashmap if clicked on button
        currentAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                int clickedRecipeId = recipePreviewList.get(pos).getRecipeId();

                // modify the map and textview
                if (selectedRecipeIdMap.containsKey(clickedRecipeId)){
                    int newCount = selectedRecipeIdMap.get(clickedRecipeId) + 1;
                    selectedRecipeIdMap.put(clickedRecipeId, newCount);
                    currentCountTextView.setText(String.valueOf(newCount));

                } else {
                    selectedRecipeIdMap.put(clickedRecipeId, 1);
                    currentCountTextView.setText(String.valueOf(1));

                    // if there is one entry in the hashmap, then this is the new one. Make the selected bar appear
                    if (selectedRecipeIdMap.size() == 1){
                        toggleSelectedBar(true);
                    }
                }

                // modify the count displayed
                updateSelectedCount();

            }
        });

        currentMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                int clickedRecipeId = recipePreviewList.get(pos).getRecipeId();

                // modify the map and textview
                if (selectedRecipeIdMap.containsKey(clickedRecipeId)){
                    int newCount = selectedRecipeIdMap.get(clickedRecipeId) - 1;

                    if (newCount > 0){
                        // decrement hashmap value
                        selectedRecipeIdMap.put(clickedRecipeId, newCount);

                    } else if (newCount == 0){
                        // remove from hashmap
                        selectedRecipeIdMap.remove(clickedRecipeId);
                    }

                    // update textview shown
                    currentCountTextView.setText(String.valueOf(newCount));

                }  // else, this button does nothing (The newCount is <0 -> Not valid)

                // update the selected count
                updateSelectedCount();

                // if the map is empty, toggle off the selected bar
                if (selectedRecipeIdMap.isEmpty()){
                    toggleSelectedBar(false);
                }
            }
        });


        // set deselect button functionality
        deselectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deselectAll();
            }
        });


        // set up delete button
        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
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
                        int pos = holder.getAdapterPosition();
                        int clickedRecipeId = recipePreviewList.get(pos).getRecipeId();

                        // delete Recipe from arraylists
                        recipePreviewList.remove(pos);

                        for (int i = 0 ; i < recipePreviewListFull.size() ; i++){
                            if (recipePreviewListFull.get(i).getRecipeId() == clickedRecipeId){
                                recipePreviewListFull.remove(i);
                                break;
                            }
                        }

                        notifyItemRemoved(pos);

                        // update displayed count
                        String countString = String.valueOf(recipePreviewList.size()) + " results";
                        TextView textView = (TextView) ((Activity) ctx).findViewById(R.id.recipeCount_textView);
                        textView.setText(countString);

                        // display empty message if all recipes are deleted
                        if (recipePreviewList.isEmpty()){
                            TextView emptyRecipeTextView = (TextView) ((Activity) ctx).findViewById(R.id.emptyRecipes_textView);
                            emptyRecipeTextView.setVisibility(View.VISIBLE);
                        }

                        // delete SL from db
                        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);
                        db.deleteRecipeFromId(clickedRecipeId);
                        db.close();

                        // deselect all, if some are selected
                        if (!selectedRecipeIdMap.isEmpty()){
                            deselectAll();
                        }

                        // dismiss dialog
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


        // set up listener for creating shopping list from recipes
        createShoppingListFromRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set up dialog
                Dialog dialog = new Dialog(ctx);
                dialog.setContentView(R.layout.create_shopping_list_window);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(getDrawable(ctx, R.drawable.custom_edit_text));
                dialog.setCancelable(false);

                // load the two buttons
                cancelButton2 = dialog.findViewById(R.id.confirmShoppingListCreateCancel_button);
                createShoppingListButton = dialog.findViewById(R.id.confirmShoppingListCreate_button);
                shoppingListNameEditText = dialog.findViewById(R.id.createShoppingListName_edittext);

                // set listener for cancel
                cancelButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Remove the dialog
                        dialog.dismiss();
                    }
                });
                
                // set listener for confirm
                createShoppingListButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String shoppingListName = shoppingListNameEditText.getText().toString();

                        // empty name
                        if (shoppingListName.isEmpty()){
                            Toast.makeText(ctx, "Shopping list name is empty", Toast.LENGTH_SHORT).show();

                        } else {
                            // pass the recipe id set to generate shopping list data
                            ShoppingListGenerator shoppingListGenerator = new ShoppingListGenerator(ctx, selectedRecipeIdMap);
                            boolean b = shoppingListGenerator.generateShoppingListFromRecipeIds(shoppingListName);

                            // deselect all checkboxes
                            deselectAll();

                            // message
                            if (b) {
                                Toast.makeText(ctx, "Shopping list successfully created", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ctx, "There are no ingredients for this shopping list", Toast.LENGTH_SHORT).show();
                            }

                            dialog.dismiss();
                        }
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
        db.close();
        return tagsValidated;
    }

    // function to turn on / off the top selected bar
    public void toggleSelectedBar(Boolean b){
        if (b) {
            selectedOptionsConstraintLayout.setVisibility(View.VISIBLE);
            selectedOptionsConstraintLayout.setEnabled(true);

            // filterOptionsLinearLayout.setVisibility(View.GONE);
            // filterOptionsLinearLayout.setEnabled(false);

        } else {
            selectedOptionsConstraintLayout.setVisibility(View.GONE);
            selectedOptionsConstraintLayout.setEnabled(false);

            // filterOptionsLinearLayout.setVisibility(View.VISIBLE);
            // filterOptionsLinearLayout.setEnabled(true);

        }
    }

    public void deselectAll(){
        // clear the hashmap
        selectedRecipeIdMap.clear();
        toggleSelectedBar(false);
        notifyDataSetChanged();   // used to reload all text views to set their text back to default (0)
    }

    public void updateSelectedCount(){
        int count = 0;
        for (int value : selectedRecipeIdMap.values()){
            count += value;
        }
        String countStr = String.valueOf(count) + " selected";
        selectedCountTextView.setText(countStr);
    }
}
