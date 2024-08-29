package com.example.recipeapp;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class EditRecipe extends AppCompatActivity {

    // recipe ID that is passed to this activity
    private int recipeId;

    // Recycler views for the recipe displays
    private RecyclerView tagsRecyclerView;
    private RecyclerView ingredientsRecyclerView;

    // Parameters for loading a recipe.
    private String name;
    private byte[] image;
    private String description;
    private float prepTime;
    private String link;

    // stores current tags that are added
    private ArrayList<String> tagList;

    // stores current list of ingredients
    private ArrayList<Ingredient> ingredientList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_recipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // set tags to new empty array
        tagList = new ArrayList<>();
        ingredientList = new ArrayList<>();

        // init the recycler views
        tagsRecyclerView = (RecyclerView) findViewById(R.id.recipeTags_recyclerView);
        ingredientsRecyclerView = (RecyclerView) findViewById(R.id.recipeIngredients_recyclerView);

        // Get the recipe id passed
        if (getIntent().hasExtra("recipe_id")) {
            recipeId = getIntent().getIntExtra("recipe_id", -1);
            Log.d("HELLO", String.valueOf(recipeId));


            // Get information from the database name, image, description, link, prep_time
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
            Cursor cursor = db.getRecipeFromId(recipeId);

            if (cursor.getCount() > 0){
                cursor.moveToNext();

                // set class variables
                name = cursor.getString(0);
                image = cursor.getBlob(1);
                description = cursor.getString(2);
                link = cursor.getString(3);
                prepTime = cursor.getFloat(4);

                // 1. Extract basic recipe data
                TextView recipeNameEditText = (TextView) findViewById(R.id.recipeName_edittext);
                recipeNameEditText.setText(name);

                ImageView recipeImageView = (ImageView) findViewById(R.id.recipeImage);
                Bitmap bm = BitmapFactory.decodeByteArray(image, 0, image.length);
                recipeImageView.setImageBitmap(bm);

                TextView recipeDescEditText = (TextView) findViewById(R.id.recipeDesc_edittext);
                recipeDescEditText.setText(description);

                TextView recipePrepTimeEditText = (TextView) findViewById(R.id.recipePrepTime_edittext);
                recipePrepTimeEditText.setText(String.valueOf(prepTime));

                TextView recipeLinkEditText = (TextView) findViewById(R.id.recipeLink_edittext);
                recipeLinkEditText.setText(link);


                // 2. Extract tags data
                Cursor cursorTags = db.getTagsFromId(recipeId);
                if (cursorTags.getCount() > 0){
                    while (cursorTags.moveToNext()){
                        tagList.add(cursorTags.getString(0));
                    }
                }
                // modify the recyclerview of tags that are displayed
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
                tagsRecyclerView.setLayoutManager(gridLayoutManager);
                tagsRecyclerView.setAdapter(new StringAdapter(this, tagList));


                // 3. Extract ingredients data
                Cursor cursorIngredients = db.getIngredientsFromId(recipeId);
                if (cursorIngredients.getCount() > 0){
                    while (cursorIngredients.moveToNext()){
                        String ingredientName = cursorIngredients.getString(0);
                        Float ingredientAmount = cursorIngredients.getFloat(1);
                        String ingredientSupermarket = cursorIngredients.getString(2);
                        Float ingredientCost = cursorIngredients.getFloat(3);

                        Ingredient ingredient = new Ingredient(ingredientName, ingredientAmount, ingredientSupermarket, ingredientCost);
                        ingredientList.add(ingredient);
                    }
                }

                // modify the recycler view that displays list of items
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                ingredientsRecyclerView.setLayoutManager(linearLayoutManager);

                IngredientAdapter ingredientRecyclerViewAdapter = new IngredientAdapter(this, ingredientList);
                ingredientsRecyclerView.setAdapter(ingredientRecyclerViewAdapter);


                // 4. Extract cost data
                Cursor cursorCost = db.getWeightedCost(recipeId);
                if (cursorCost.getCount() > 0){
                    cursorCost.moveToNext();
                    float weightedCost = cursorCost.getFloat(0);

                    TextView totalCostTextView = (TextView) findViewById(R.id.totalCost_textView);
                    String totalCostString = "Total weighted cost: " + String.format(Locale.US, "%.2f", weightedCost);
                    totalCostTextView.setText(totalCostString);
                }

            } else {
                Toast.makeText(getApplicationContext(), "Recipe not found", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // Return to previous activity
    public void exitActivity(View v){
        getOnBackPressedDispatcher().onBackPressed();
    }
}