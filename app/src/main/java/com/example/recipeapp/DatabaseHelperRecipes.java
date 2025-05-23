package com.example.recipeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// the database helper for recipes.
public class DatabaseHelperRecipes extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "recipeAppRecipes.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelperRecipes(Context context) {
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
                        "is_favourited BOOLEAN DEFAULT FALSE" +
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
                        "amount FLOAT CHECK(amount >= 0)," +
                        "supermarket VARCHAR(50)," +
                        "cost FLOAT CHECK(cost >= 0), " +
                        "shelf_life INT CHECK(shelf_life >= 0) DEFAULT 365" +
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

        String createMeals =
                "CREATE TABLE IF NOT EXISTS Meals(" +
                        "meal_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "date TEXT," +
                        "category VARCHAR(50)," +
                        "recipe_id INTEGER NOT NULL," +
                        "FOREIGN KEY (recipe_id) REFERENCES Recipes(recipe_id) ON DELETE CASCADE" +
                        ");";

        db.execSQL(createRecipes);
        db.execSQL(createTags);
        db.execSQL(createIngredients);
        db.execSQL(createRecipeTags);
        db.execSQL(createRecipeIngredients);
        db.execSQL(createMeals);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Recipes;");
        db.execSQL("DROP TABLE IF EXISTS Tags;");
        db.execSQL("DROP TABLE IF EXISTS Ingredients;");
        db.execSQL("DROP TABLE IF EXISTS Recipe_tags;");
        db.execSQL("DROP TABLE IF EXISTS Recipe_ingredients;");
        db.execSQL("DROP TABLE IF EXISTS Meals;");
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
            int shelfLife = ingredient.getShelfLife();

            contentValuesIngredients.put("name", ingredientName);
            contentValuesIngredients.put("amount", amount);
            contentValuesIngredients.put("supermarket", supermarket);
            contentValuesIngredients.put("cost", cost);
            contentValuesIngredients.put("shelf_life", shelfLife);

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

    // method to extract information needed to display the recipes, given the ordering option.
    public Cursor getRecipes(int orderingOption) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Extract all recipe data
        // the 3rd item is the times cooked of that recipe
        String queryRecipe;

        // different ordering methods based on the ordering option
        switch(orderingOption){
            case 1:
                queryRecipe = "SELECT R.recipe_id, R.name, prep_time, COALESCE(COUNT(m.meal_id), 0) AS times_cooked, is_favourited, image " +
                        "FROM Recipes R LEFT JOIN Meals m ON R.recipe_id = m.recipe_id " +
                        "LEFT JOIN Recipe_ingredients RI ON R.recipe_id = RI.recipe_id "+
                        "LEFT JOIN Ingredients I ON RI.ingredient_id = I.ingredient_id "+
                        "GROUP BY R.recipe_id " +
                        "ORDER BY (SELECT ROUND(SUM(I.cost*I.amount), 2)) DESC;";
                break;

            case 2:
                queryRecipe = "SELECT r.recipe_id, name, prep_time, COALESCE(COUNT(m.meal_id), 0) AS times_cooked, is_favourited, image " +
                        "FROM Recipes r LEFT JOIN Meals m ON r.recipe_id = m.recipe_id " +
                        "GROUP BY R.recipe_id " +
                        "ORDER BY times_cooked ASC;";
                break;

            case 3:
                queryRecipe = "SELECT r.recipe_id, name, prep_time, COALESCE(COUNT(m.meal_id), 0) AS times_cooked, is_favourited, image " +
                        "FROM Recipes r LEFT JOIN Meals m ON r.recipe_id = m.recipe_id " +
                        "GROUP BY R.recipe_id " +
                        "ORDER BY prep_time DESC;";
                break;

            default:
                queryRecipe = "SELECT r.recipe_id, name, prep_time, COALESCE(COUNT(m.meal_id), 0) AS times_cooked, is_favourited, image " +
                        "FROM Recipes r LEFT JOIN Meals m ON r.recipe_id = m.recipe_id " +
                        "GROUP BY r.recipe_id; ";
                break;
        }

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
        db.execSQL(update, new String[]{String.valueOf(recipeId)});
    }

    public void updateRecipeUnFavourite(int recipeId){
        SQLiteDatabase db = this.getWritableDatabase();

        String update = "UPDATE Recipes SET is_favourited = FALSE WHERE recipe_id = ?;";
        db.execSQL(update, new String[]{String.valueOf(recipeId)});
    }


    /*
    These methods are used to extract data in the addNewRecipe activity
    that is triggered by clicking on an item in the recyclerview.
    These are not used when creating a recipe.
     */

    // methods to extract recipe data (Those attributes in Recipes table) when given a recipe ID. Used to display recipe data.
    public Cursor getRecipeFromId(int recipeId){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT name, image, description, link, prep_time, COALESCE(COUNT(m.meal_id), 0) AS times_cooked " +
                "FROM Recipes r LEFT JOIN Meals m ON r.recipe_id = m.recipe_id " +
                "WHERE r.recipe_id = ? " +
                "GROUP BY r.recipe_id;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{String.valueOf(recipeId)});
        }
        return cursor;
    }

    public Cursor getTagsFromId(int recipeId){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT T.name " +
                "FROM Recipes R JOIN Recipe_tags RT ON R.recipe_id = RT.recipe_id " +
                "JOIN Tags T ON RT.tag_id = T.tag_id " +
                "WHERE R.recipe_id = ?;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{String.valueOf(recipeId)});
        }
        return cursor;
    }

    public Cursor getIngredientsFromId(int recipeId){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT I.name, I.amount, I.supermarket, I.cost, I.shelf_life, I.ingredient_id " +
                "FROM Recipes R JOIN Recipe_ingredients RI ON R.recipe_id = RI.recipe_id " +
                "JOIN Ingredients I ON RI.ingredient_id = I.ingredient_id " +
                "WHERE R.recipe_id = ?;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{String.valueOf(recipeId)});
        }
        return cursor;
    }

    // Use getWeightedCost() to extract the weighted cost of ingredients

    /*
    These methods are used to update data when the done button
    in the addnewrecipe activity is clicked.
     */

    // update the recipe (name image desc link prepTime)
    // when the done button is clicked when editing a recipe.
    public boolean updateRecipeFromId(int recipeId, String name, byte[] image, String description, String link, float prepTime){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("image", image);
        contentValues.put("description", description);
        contentValues.put("link", link);
        contentValues.put("prep_time", prepTime);

        String whereClause = "recipe_id = ?";
        String[] whereArgs = new String[]{String.valueOf(recipeId)};

        // use the update method to update sql
        // return the number of rows affected
        db.update("Recipes", contentValues, whereClause, whereArgs);

        return true;
    }

    public boolean updateRecipeTagsFromId(int recipeId, ArrayList<String> tagList){
        SQLiteDatabase db = this.getWritableDatabase();

        // first delete all old tags data
        String del1 = "DELETE FROM Recipe_tags WHERE recipe_id = ?;";
        db.execSQL(del1, new String[]{String.valueOf(recipeId)});

        // these tags are those tags with recipe id = given one because those that are in are deleted above
        String del2 = "DELETE FROM Tags WHERE tag_id NOT IN (SELECT tag_id FROM Recipe_tags);";
        db.execSQL(del2);

        // then insert new tags data
        ContentValues contentValuesTags = new ContentValues();
        ContentValues contentValuesRecipeTags = new ContentValues();

        // insert into tags and recipe tags
        for (String tag : tagList) {
            contentValuesTags.put("name", tag);
            long resultTags = db.insert("Tags", null, contentValuesTags);
            if (resultTags == -1) {
                return false;
            }

            contentValuesRecipeTags.put("recipe_id", recipeId);
            contentValuesRecipeTags.put("tag_id", resultTags);
            long resultRecipeTags = db.insert("Recipe_tags", null, contentValuesRecipeTags);
            if (resultRecipeTags == -1) {
                return false;
            }
        }

        // return true if successful
        return true;
    }

    public boolean updateRecipeIngredientsFromId(int recipeId, ArrayList<Ingredient> ingredientList){
        SQLiteDatabase db = this.getWritableDatabase();

        // first delete all old tags data
        String del1 = "DELETE FROM Recipe_ingredients WHERE recipe_id = ?;";
        db.execSQL(del1, new String[]{String.valueOf(recipeId)});

        String del2 = "DELETE FROM Ingredients WHERE ingredient_id NOT IN (SELECT ingredient_id FROM Recipe_ingredients);";
        db.execSQL(del2);


        ContentValues contentValuesIngredients = new ContentValues();
        ContentValues contentValuesRecipeIngredients = new ContentValues();

        // insert into ingredients and recipe ingredients
        for (Ingredient ingredient : ingredientList) {
            String ingredientName = ingredient.getIngredient();
            Float amount = ingredient.getAmount();
            String supermarket = ingredient.getSupermarket();
            Float cost = ingredient.getCost();
            int shelfLife = ingredient.getShelfLife();

            contentValuesIngredients.put("name", ingredientName);
            contentValuesIngredients.put("amount", amount);
            contentValuesIngredients.put("supermarket", supermarket);
            contentValuesIngredients.put("cost", cost);
            contentValuesIngredients.put("shelf_life", shelfLife);

            long resultIngredients = db.insert("Ingredients", null, contentValuesIngredients);
            if (resultIngredients == -1) {
                return false;
            }

            contentValuesRecipeIngredients.put("recipe_id", recipeId);
            contentValuesRecipeIngredients.put("ingredient_id", resultIngredients);
            long resultRecipeIngredients = db.insert("Recipe_ingredients", null, contentValuesRecipeIngredients);
            if (resultRecipeIngredients == -1) {
                return false;
            }
        }

        return true;
    }

    // method to delete a recipe from the delete button in the recycler view.
    public void deleteRecipeFromId(int recipeId){

        SQLiteDatabase db = this.getWritableDatabase();

        // delete the rows from recipe_tags and recipe_ingredients
        String delTag1 = "DELETE FROM Recipe_tags WHERE recipe_id = ?;";
        db.execSQL(delTag1, new String[]{String.valueOf(recipeId)});

        String delIng1 = "DELETE FROM Recipe_ingredients WHERE recipe_id = ?;";
        db.execSQL(delIng1, new String[]{String.valueOf(recipeId)});

        // delete the tags and ingredients associated with that recipe
        String delTag2 = "DELETE FROM Tags WHERE tag_id NOT IN (SELECT tag_id FROM Recipe_tags);";
        db.execSQL(delTag2);

        String delIng2 = "DELETE FROM Ingredients WHERE ingredient_id NOT IN (SELECT ingredient_id FROM Recipe_ingredients);";
        db.execSQL(delIng2);

        // delete the recipe itself
        String del = "DELETE FROM Recipes WHERE recipe_id = ?;";
        db.execSQL(del, new String[]{String.valueOf(recipeId)});
    }



    // method to add new meal
    public boolean addMeal(String dateString, String category, int recipeId){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValuesMeals = new ContentValues();

        // set new values submitted
        contentValuesMeals.put("date", dateString);
        contentValuesMeals.put("category", category);
        contentValuesMeals.put("recipe_id", recipeId);

        // insert into recipes - returns the auto incremented id
        long result = db.insert("Meals", null, contentValuesMeals);

        return result != -1;
    }

    // method to remove new meal
    public void deleteMealFromId(int mealId){
        SQLiteDatabase db = this.getWritableDatabase();

        String del = "DELETE FROM Meals WHERE meal_id = ?;";
        db.execSQL(del, new String[]{String.valueOf(mealId)});
    }

    // method to get meal preview information about meals from date: Category and recipe name
    // for cost of recipes, use the separate method cost from RID
    public Cursor getMealsFromDate(String dateString){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT M.meal_id, M.category, R.name, R.image, R.recipe_id " +
                "FROM Meals M JOIN Recipes R ON M.recipe_id = R.recipe_id " +
                "WHERE M.date = ?;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{dateString});
        }
        return cursor;
    }

    // get all recipe ids that belong to a specific date
    public Cursor getRecipeIdsFromDate(String dateString){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT recipe_id FROM Meals WHERE date = ?; ";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{dateString});
        }
        return cursor;
    }


    // get all ingredients by looking at every meal before the date specified.
    public Cursor getIngredientsFromMealTillDate(String dateString){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT I.ingredient_id, I.name, I.amount, I.supermarket, I.cost, I.shelf_life, M.date AS date " +
                "FROM Meals M JOIN Recipes R ON M.recipe_id = R.recipe_id " +
                "JOIN Recipe_ingredients RI ON R.recipe_id = RI.recipe_id " +
                "JOIN Ingredients I ON RI.ingredient_id = I.ingredient_id " +
                "WHERE date <= ? ORDER BY date ASC;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{dateString});
        }
        return cursor;
    }

    // method to return the recipe count. Used to display on the recipe fragment page
    public Cursor getMealCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        String queryMealCount = "SELECT COUNT(*) FROM Meals;";
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryMealCount, null);
        }
        return cursor;
    }

    // method to return all registered ingredients -- in a way that uniquely identifies them
    // an ingredient is uniquely identified by (name, supermarket, cost, shelfLife)
    public Cursor getIngredients(){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT name, supermarket, cost, shelf_life FROM Ingredients;";
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    // method to clear recipes
    public void clearRecipes(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Recipes;");
        db.execSQL("DROP TABLE IF EXISTS Tags;");
        db.execSQL("DROP TABLE IF EXISTS Ingredients;");
        db.execSQL("DROP TABLE IF EXISTS Recipe_tags;");
        db.execSQL("DROP TABLE IF EXISTS Recipe_ingredients;");
        db.execSQL("DROP TABLE IF EXISTS Meals;");
        onCreate(db);     // recreate the database
        Toast.makeText(context, "Recipes reset", Toast.LENGTH_SHORT).show();
    }

    // method to clear meals
    public void clearMeals(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Meals;");
        onCreate(db);     // recreate the database
        Toast.makeText(context, "Meals reset", Toast.LENGTH_SHORT).show();
    }


    // method to get the analytics
    public Cursor getCostAfterDate(String dateString, String currentDate){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT ROUND(SUM(I.cost*I.amount), 2) AS total_cost " +
                "FROM Recipes R JOIN Meals M ON R.recipe_id = M.recipe_id " +
                "JOIN Recipe_ingredients RI ON R.recipe_id = RI.recipe_id "+
                "JOIN Ingredients I ON RI.ingredient_id = I.ingredient_id " +
                "WHERE M.date > ? AND M.date <= ?;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{dateString, currentDate});
        }
        return cursor;
    }

    // method to get all distinct supermarkets
    public Cursor getDistinctSupermarkets(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        // Extract all SL data
        String queryShoppingList = "SELECT DISTINCT(supermarket) " +
                "FROM Ingredients I JOIN Recipe_ingredients RI " +
                "ON I.ingredient_id = RI.ingredient_id " +
                "WHERE RI.recipe_id = ?;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryShoppingList, new String[]{String.valueOf(id)});
        }
        return cursor;
    }
}
