package com.example.recipeapp;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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

                // modify the text views
                TextView recipeNameEditText = findViewById(R.id.recipeName_edittext);
                recipeNameEditText.setText(name);
            }

        }
    }

    // Return to previous activity
    public void exitActivity(View v){
        getOnBackPressedDispatcher().onBackPressed();
    }
}