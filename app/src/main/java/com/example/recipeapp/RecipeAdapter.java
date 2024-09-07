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

                    if (newCount >= 0){
                        selectedRecipeIdMap.put(clickedRecipeId, newCount);
                        currentCountTextView.setText(String.valueOf(newCount));

                    }

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

        /*
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

                        // display empty message if all recipes are deleted
                        if (recipePreviewList.isEmpty()){
                            TextView emptyRecipeTextView = (TextView) ((Activity) ctx).findViewById(R.id.emptyRecipes_textView);
                            emptyRecipeTextView.setVisibility(View.VISIBLE);
                        }

                        // Remove the dialog
                        dialog.dismiss();

                        // deselect all and return the original bars
                        deselectAll();

                    }
                });

                dialog.show();
            }
        });

         */


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
                            LinkedHashMap<String, ArrayList<ShoppingListIngredient>> map = generateShoppingListFromRecipeIds(selectedRecipeIdMap);

                            // add to db
                            DatabaseHelperShoppingLists db = new DatabaseHelperShoppingLists(ctx);
                            db.addShoppingList(shoppingListName, "", map);

                            // deselect all checkboxes
                            deselectAll();

                            // message
                            Toast.makeText(ctx, "Shopping list successfully created", Toast.LENGTH_SHORT).show();

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


    /**
     * method to generate shopping list ingredients from a set of recipe ids.
     * 2 ingredients are considered identical if their NAME (CASE SENSITIVE), COST and SUPERMARKET are the same.
     * Hence, (supermarket, name, cost) uniquely identifies an ingredient.
     * It is stored before elements are added to the final hashMap.
     *
     * @param selectedRecipeIdMap a hashmap where key is the recipeId, value is the count of it.
     * @return a linkedhashmap that is ready to be put into the database
     */
    public LinkedHashMap<String, ArrayList<ShoppingListIngredient>> generateShoppingListFromRecipeIds(HashMap<Integer, Integer> selectedRecipeIdMap){

        // 1. add all ingredients to this hashset to uniquely identify them first
        // key = triple, value = amount
        HashMap<Triple<String, String, Float>, Float> uniqueIngredientsMap = new HashMap<>();
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);

        // extract information from each recipe id and put them into hashmap
        String name;
        Float amount;
        String supermarket;
        Float cost;

        for (Map.Entry<Integer, Integer> entry : selectedRecipeIdMap.entrySet()){
            int recipeId = entry.getKey();
            int count = entry.getValue();

            Cursor cursor = db.getIngredientsFromId(recipeId);
            if (cursor.getCount() > 0){
                while (cursor.moveToNext()){
                    name = cursor.getString(0);
                    amount = cursor.getFloat(1)*count;
                    supermarket = cursor.getString(2);
                    cost = cursor.getFloat(3)*count;

                    // add triples to hashset
                    Triple<String, String, Float> triple = new Triple<>(supermarket, name, cost);
                    if (uniqueIngredientsMap.containsKey(triple)){
                        Float originalAmount = uniqueIngredientsMap.get(triple);
                        uniqueIngredientsMap.put(triple, originalAmount + amount);

                    } else {
                        uniqueIngredientsMap.put(triple, amount);
                    }

                }
            }
        }

        // 2. Create the LinkedHashMap from the set of ingredient triples
        LinkedHashMap<String, ArrayList<ShoppingListIngredient>> shoppingListIngredientMap = new LinkedHashMap<>();

        for (Map.Entry<Triple<String, String, Float>, Float> entry : uniqueIngredientsMap.entrySet()){
            Triple<String, String, Float> key = entry.getKey();
            String finalSupermarket = key.getFirst();
            String finalIngredientName = key.getSecond();
            Float finalCost = key.getThird();

            Float totalAmount = entry.getValue();

            // round up the total amount
            int totalAmountRoundedUp = (int) Math.ceil(totalAmount);

            // create ShoppingListIngredient
            ShoppingListIngredient shoppingListIngredient = new ShoppingListIngredient(finalIngredientName, totalAmountRoundedUp, finalCost, false);

            // add the ShoppingListIngredient to the linked hash map
            if (shoppingListIngredientMap.containsKey(finalSupermarket)){
                shoppingListIngredientMap.get(finalSupermarket).add(shoppingListIngredient);

            } else {
                ArrayList<ShoppingListIngredient> arrayList = new ArrayList<>();
                arrayList.add(shoppingListIngredient);
                shoppingListIngredientMap.put(finalSupermarket, arrayList);

            }
        }
        return shoppingListIngredientMap;
    }
}
