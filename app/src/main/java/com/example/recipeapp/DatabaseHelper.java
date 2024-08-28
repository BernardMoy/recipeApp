package com.example.recipeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "recipeApp.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the database schema
        String createRecipes =
                "CREATE TABLE IF NOT EXISTS Recipes (" +
                        "recipe_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "image BLOB, " +
                        "description TEXT," +
                        "link TEXT," +
                        "prep_time FLOAT," +
                        "is_favourited BOOLEAN DEFAULT FALSE," +
                        "times_cooked INTEGER DEFAULT 0" +
                        ");";

        String createTags =
                "CREATE TABLE IF NOT EXISTS Tags (" +
                        "tag_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name VARCHAR(20) NOT NULL" +
                        ");";

        String createIngredients =
                "CREATE TABLE IF NOT EXISTS Ingredients (" +
                        "ingredient_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name VARCHAR(50) NOT NULL," +
                        "amount FLOAT," +
                        "supermarket VARCHAR(50)," +
                        "cost FLOAT" +
                        ");";

        String createRecipeTags =
                "CREATE TABLE IF NOT EXISTS Recipe_tags(" +
                        "recipe_id INTEGER," +
                        "tag_id INTEGER," +
                        "FOREIGN KEY (recipe_id) REFERENCES Recipes(recipe_id) ON DELETE CASCADE," +
                        "FOREIGN KEY (tag_id) REFERENCES Tags(tag_id) ON DELETE CASCADE" +
                        ");";

        String createRecipeIngredients =
                "CREATE TABLE IF NOT EXISTS Recipe_ingredients(" +
                        "recipe_id INTEGER," +
                        "ingredient_id INTEGER," +
                        "FOREIGN KEY (recipe_id) REFERENCES Recipes(recipe_id) ON DELETE CASCADE," +
                        "FOREIGN KEY (ingredient_id) REFERENCES Ingredients(ingredient_id) ON DELETE CASCADE" +
                        ");";

        db.execSQL(createRecipes);
        db.execSQL(createTags);
        db.execSQL(createIngredients);
        db.execSQL(createRecipeTags);
        db.execSQL(createRecipeIngredients);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Recipes;");
        db.execSQL("DROP TABLE IF EXISTS Tags;");
        db.execSQL("DROP TABLE IF EXISTS Ingredients;");
        db.execSQL("DROP TABLE IF EXISTS Recipe_tags;");
        db.execSQL("DROP TABLE IF EXISTS Recipe_ingredients;");
    }

    // reset database
    public void reset() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Recipes;");
        db.execSQL("DROP TABLE IF EXISTS Tags;");
        db.execSQL("DROP TABLE IF EXISTS Ingredients;");
        db.execSQL("DROP TABLE IF EXISTS Recipe_tags;");
        db.execSQL("DROP TABLE IF EXISTS Recipe_ingredients;");
        onCreate(db);     // recreate the database
        Toast.makeText(context, "Database reset", Toast.LENGTH_SHORT).show();
    }

    // method to add new recipe
    public boolean addRecipe(String name, byte[] image, String description, String link, float prepTime, List<String> tagList, List<Ingredient> ingredientList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValuesRecipes = new ContentValues();
        ContentValues contentValuesTags = new ContentValues();
        ContentValues contentValuesIngredients = new ContentValues();
        ContentValues contentValuesRecipeTags = new ContentValues();
        ContentValues contentValuesRecipeIngredients = new ContentValues();

        // set new values submitted
        contentValuesRecipes.put("name", name);
        contentValuesRecipes.put("image", image);
        contentValuesRecipes.put("description", description);
        contentValuesRecipes.put("link", link);
        contentValuesRecipes.put("prep_time", prepTime);

        // insert into recipes - returns the auto incremented id
        long resultRecipes = db.insert("Recipes", null, contentValuesRecipes);
        if (resultRecipes == -1) {
            return false;
        }

        // insert into tags and recipe tags
        for (String tag : tagList) {
            contentValuesTags.put("name", tag);
            long resultTags = db.insert("Tags", null, contentValuesTags);
            if (resultTags == -1) {
                return false;
            }

            contentValuesRecipeTags.put("recipe_id", resultRecipes);
            contentValuesRecipeTags.put("tag_id", resultTags);
            long resultRecipeTags = db.insert("Recipe_tags", null, contentValuesRecipeTags);
            if (resultRecipeTags == -1) {
                return false;
            }
        }

        // insert into ingredients and recipe ingredients
        for (Ingredient ingredient : ingredientList) {
            String ingredientName = ingredient.getIngredient();
            Float amount = ingredient.getAmount();
            String supermarket = ingredient.getSupermarket();
            Float cost = ingredient.getCost();

            contentValuesIngredients.put("name", ingredientName);
            contentValuesIngredients.put("amount", amount);
            contentValuesIngredients.put("supermarket", supermarket);
            contentValuesIngredients.put("cost", cost);

            long resultIngredients = db.insert("Ingredients", null, contentValuesIngredients);
            if (resultIngredients == -1) {
                return false;
            }

            contentValuesRecipeIngredients.put("recipe_id", resultRecipes);
            contentValuesRecipeIngredients.put("ingredient_id", resultIngredients);
            long resultRecipeIngredients = db.insert("Recipe_ingredients", null, contentValuesRecipeIngredients);
            if (resultRecipeIngredients == -1) {
                return false;
            }

        }

        return true;
    }

    // method to extract information needed to display the recipes
    public Cursor getRecipes() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Extract all recipe data
        String queryRecipe = "SELECT recipe_id, name, prep_time, times_cooked, is_favourited, image FROM Recipes";
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryRecipe, null);
        }
        return cursor;
    }

    // method to return the recipe count. Used to display on the recipe fragment page
    public Cursor getRecipesCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        String queryRecipesCount = "SELECT COUNT(*) FROM Recipes;";
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryRecipesCount, null);
        }
        return cursor;
    }

    // method to extract the tags count
    public Cursor getTagsCount(int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Extract all tags data
        String queryTagsCount = "SELECT COUNT(*) " +
                "FROM Tags T JOIN Recipe_tags RT ON T.tag_id = RT.tag_id " +
                "WHERE RT.recipe_id = ?;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryTagsCount, new String[]{String.valueOf(recipeId)});
        }
        return cursor;
    }

    // method to extract the first tag when given a recipeid
    public Cursor getTagPreview(int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Extract all tags data
        String queryTag = "SELECT T.name " +
                "FROM Tags T JOIN Recipe_tags RT ON T.tag_id = RT.tag_id " +
                "WHERE RT.recipe_id = ? " +
                "LIMIT 1;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryTag, new String[]{String.valueOf(recipeId)});
        }
        return cursor;
    }

    // method to extract the weighted cost
    public Cursor getWeightedCost(int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String queryCost = "SELECT ROUND(SUM(I.cost*I.amount), 2) " +
                "FROM Ingredients I JOIN Recipe_ingredients RI ON I.ingredient_id = RI.ingredient_id " +
                "WHERE RI.recipe_id = ?;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryCost, new String[]{String.valueOf(recipeId)});
        }
        return cursor;
    }

    // method to extract all tags
    public Cursor getTags() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Extract all recipe data
        String queryRecipe = "SELECT DISTINCT name FROM Tags;";
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryRecipe, null);
        }
        return cursor;
    }

    // method to extract tags from recipe id
    public Cursor getTagsFromRecipeId(int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Extract all recipe data
        String queryTag = "SELECT DISTINCT T.name " +
                "FROM Tags T JOIN Recipe_tags RT ON T.tag_id = RT.tag_id " +
                "WHERE RT.recipe_id = ? ";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryTag, new String[]{String.valueOf(recipeId)});
        }
        return cursor;
    }

    // method to mark recipes favourited or unfavourited.
    public void updateRecipeFavourite(int recipeId){
        SQLiteDatabase db = this.getWritableDatabase();

        // mark recipe as fav
        String update = "UPDATE Recipes SET is_favourited = TRUE WHERE recipe_id = ?;";
        Cursor cursor = db.rawQuery(update, new String[]{String.valueOf(recipeId)});
        cursor.close();
    }

    public void updateRecipeUnFavourite(int recipeId){
        SQLiteDatabase db = this.getWritableDatabase();

        String update = "UPDATE Recipes SET is_favourited = FALSE WHERE recipe_id = ?;";
        Cursor cursor = db.rawQuery(update, new String[]{String.valueOf(recipeId)});
        cursor.close();
    }

}
