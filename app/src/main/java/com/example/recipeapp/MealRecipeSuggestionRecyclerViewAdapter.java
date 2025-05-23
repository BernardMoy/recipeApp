package com.example.recipeapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    // the done button
    private Button doneButton;

    // store the current date string passed from add new meal activity
    private String dateString;

    // store the suggested recipe id. This is passed to this adapter when it is updated
    private int suggestedRecipeId;
    private Button suggestionSelectButton;


    public MealRecipeSuggestionRecyclerViewAdapter(Context ctx, ArrayList<MealRecipeSuggestionPreview> previewList, String dateString, int suggestedRecipeId){
        this.ctx = ctx;
        this.previewListFull = previewList;
        this.previewList = new ArrayList<>(previewListFull);
        this.dateString = dateString;
        this.suggestedRecipeId = suggestedRecipeId;
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
        selectedRecipeId = -1;
        selectedImageView = ((Activity) ctx).findViewById(R.id.selectedImage_imageView);
        selectedNameTextView = ((Activity) ctx).findViewById(R.id.selectedName_textView);
        selectedCostTextView = ((Activity) ctx).findViewById(R.id.selectedCost_textView);
        suggestionSelectButton = ((Activity) ctx).findViewById(R.id.suggestionSelect_button);

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



        // set on click listener for the suggestion select button, if not -1
        if (suggestedRecipeId != -1){
            suggestionSelectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // iterate the preview list FULL to find the one with the recipe id = suggested id
                    MealRecipeSuggestionPreview selectedRecipe = previewListFull.get(0);

                    for (MealRecipeSuggestionPreview preview1 : previewListFull){
                        if (preview1.getRecipeId() == suggestedRecipeId){
                            selectedRecipe = preview1;
                            break;
                        }
                    }
                    selectedRecipeId = suggestedRecipeId;  // modify global var

                    // modify the selected data
                    byte[] selectedImageByteArray = selectedRecipe.getRecipeImage();
                    Bitmap bm = BitmapFactory.decodeByteArray(selectedImageByteArray, 0, selectedImageByteArray.length);
                    selectedImageView.setImageBitmap(bm);

                    selectedNameTextView.setText(selectedRecipe.getRecipeName());

                    selectedCostTextView.setText(String.valueOf(selectedRecipe.getRecipeCost()));
                }
            });

        } else {
            suggestionSelectButton.setOnClickListener(null);

        }




        // set the on click listener for the DONE button
        doneButton = ((Activity) ctx).findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the category and the recipeID (Stored here)
                AutoCompleteTextView a = ((Activity) ctx).findViewById(R.id.category_autoCompleteTextView);
                String inputCategory = a.getText().toString();

                // check constraints
                if (inputCategory.isEmpty()){
                    inputCategory = "No category";
                }
                if (selectedRecipeId == -1){
                    Toast.makeText(ctx, "No recipe selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                // add the recipeID data to the database
                DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);
                boolean status = db.addMeal(dateString, inputCategory, selectedRecipeId);
                if (!status){
                    Toast.makeText(ctx, "Data adding failed", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ctx, "Meal added", Toast.LENGTH_SHORT).show();
                    ((Activity) ctx).onBackPressed();  // exit activity
                }

                db.close();

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
