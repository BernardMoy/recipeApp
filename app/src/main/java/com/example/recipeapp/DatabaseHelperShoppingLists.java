package com.example.recipeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// the database helper for recipes.
public class DatabaseHelperShoppingLists extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "recipeAppShoppingLists.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelperShoppingLists(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the database schema
        String createShoppingLists =
                "CREATE TABLE IF NOT EXISTS Shopping_lists (" +
                        "shopping_list_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "description TEXT," +
                        "is_favourited BOOLEAN DEFAULT FALSE" +
                        ");";

        String createIngredients =
                "CREATE TABLE IF NOT EXISTS Ingredients (" +
                        "ingredient_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "amount INT CHECK(amount >= 1), " +
                        "cost FLOAT CHECK(cost >= 0), " +
                        "checked BOOLEAN DEFAULT FALSE " +
                        ");";

        String createShoppingListSupermarketIngredients =
                "CREATE TABLE IF NOT EXISTS Shopping_list_supermarket_ingredients (" +
                        "shopping_list_id INTEGER NOT NULL, " +
                        "ingredient_id INTEGER NOT NULL, " +
                        "supermarket VARCHAR(50) NOT NULL, " +
                        "FOREIGN KEY (shopping_list_id) REFERENCES Shopping_lists(shopping_list_id), " +
                        "FOREIGN KEY (ingredient_id) REFERENCES Ingredients(ingredient_id) " +
                        ");";

        db.execSQL(createShoppingLists);
        db.execSQL(createIngredients);
        db.execSQL(createShoppingListSupermarketIngredients);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Shopping_lists;");
        db.execSQL("DROP TABLE IF EXISTS Ingredients;");
        db.execSQL("DROP TABLE IF EXISTS Shopping_list_supermarket_ingredients;");
    }

    // reset database
    public void reset() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Shopping_lists;");
        db.execSQL("DROP TABLE IF EXISTS Ingredients;");
        db.execSQL("DROP TABLE IF EXISTS Shopping_list_supermarket_ingredients;");

        onCreate(db);     // recreate the database
        Toast.makeText(context, "Shopping list database reset", Toast.LENGTH_SHORT).show();
    }


    // method to extract information needed to display the shopping lists
    public Cursor getShoppingLists() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Extract all SL data
        String queryShoppingList = "SELECT SL.name, COUNT(I.ingredient_id) AS itemCount, COUNT(DISTINCT supermarket) AS supermarketCount, SUM(I.cost) as cost, is_favourited " +
                "FROM Shopping_lists SL "+
                "JOIN Shopping_list_supermarket_ingredients SLSI ON SL.shopping_list_id = SLSI.shopping_list_id "+
                "JOIN Ingredients I ON SLSI.ingredient_id = I.ingredient_id; ";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryShoppingList, null);
        }
        return cursor;
    }

    public boolean addShoppingList(String name, String description, LinkedHashMap<String, ArrayList<ShoppingListIngredient>> shoppingListIngredientsHashMap){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValuesShoppingLists = new ContentValues();
        ContentValues contentValuesIngredients = new ContentValues();
        ContentValues contentValuesShoppingListSupermarketIngredients = new ContentValues();

        // content values for basic SL table
        contentValuesShoppingLists.put("name", name);
        contentValuesShoppingLists.put("description", description);

        // insert into SL -- get the returned id
        long resultShoppingLists = db.insert("Shopping_lists", null, contentValuesShoppingLists);
        if (resultShoppingLists == -1){
            return false;
        }

        // insert into the SLSI and Ingredient table
        for (Map.Entry<String, ArrayList<ShoppingListIngredient>> entry : shoppingListIngredientsHashMap.entrySet()) {
            String key = entry.getKey();
            ArrayList<ShoppingListIngredient> value = entry.getValue();

            for (ShoppingListIngredient ingredient : value) {
                contentValuesIngredients.put("name", ingredient.getIngredient());
                contentValuesIngredients.put("amount", ingredient.getAmount());
                contentValuesIngredients.put("cost", ingredient.getCost());

                // insert into ingredients table
                long resultIngredients = db.insert("Ingredients", null, contentValuesIngredients);
                if (resultIngredients == -1){
                    return false;
                }

                // insert into supermarket ingredients table
                contentValuesShoppingListSupermarketIngredients.put("shopping_list_id", resultShoppingLists);
                contentValuesShoppingListSupermarketIngredients.put("ingredient_id", resultIngredients);
                contentValuesShoppingListSupermarketIngredients.put("supermarker", key);
            }
        }

        return true;
    }
}
