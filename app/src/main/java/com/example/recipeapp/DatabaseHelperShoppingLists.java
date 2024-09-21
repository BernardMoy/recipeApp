package com.example.recipeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// the database helper for shoppingList.
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
    public void clearShoppingLists() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Shopping_lists;");
        db.execSQL("DROP TABLE IF EXISTS Ingredients;");
        db.execSQL("DROP TABLE IF EXISTS Shopping_list_supermarket_ingredients;");

        onCreate(db);     // recreate the database
        Toast.makeText(context, "Shopping lists reset", Toast.LENGTH_SHORT).show();
    }


    // method to extract basic information needed to display the shopping lists
    public Cursor getShoppingLists() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Extract all SL data
        String queryShoppingList = "SELECT shopping_list_id, name, is_favourited " +
                "FROM Shopping_lists;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryShoppingList, null);
        }
        return cursor;
    }

    // method for displaying counts (itemCount, supermarketCount and Cost) which needs to be separated bc there may be no data
    // method to extract information needed to display the shopping lists
    public Cursor getShoppingListsNumsFromId(int shoppingListId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Extract all SL data
        String queryShoppingList = "SELECT SUM(I.amount), COUNT(DISTINCT supermarket), SUM(I.cost*I.amount) " +
                "FROM Shopping_lists SL "+
                "JOIN Shopping_list_supermarket_ingredients SLSI ON SL.shopping_list_id = SLSI.shopping_list_id "+
                "JOIN Ingredients I ON SLSI.ingredient_id = I.ingredient_id " +
                "WHERE SL.shopping_list_id = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryShoppingList, new String[]{String.valueOf(shoppingListId)});
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
                contentValuesIngredients.put("checked", ingredient.isChecked());

                // insert into ingredients table
                long resultIngredients = db.insert("Ingredients", null, contentValuesIngredients);
                if (resultIngredients == -1){
                    return false;
                }

                // insert into supermarket ingredients table
                contentValuesShoppingListSupermarketIngredients.put("shopping_list_id", resultShoppingLists);
                contentValuesShoppingListSupermarketIngredients.put("ingredient_id", resultIngredients);
                contentValuesShoppingListSupermarketIngredients.put("supermarket", key);

                // insert into supermarket ingredients table
                long resultSupermarketIngredients = db.insert("Shopping_list_supermarket_ingredients", null, contentValuesShoppingListSupermarketIngredients);
                if (resultSupermarketIngredients == -1){
                    return false;
                }
            }
        }

        return true;
    }

    public boolean updateShoppingListFromId(int shoppingListId, String name, String description, LinkedHashMap<String, ArrayList<ShoppingListIngredient>> shoppingListIngredientsHashMap){
        SQLiteDatabase db = this.getWritableDatabase();

        // 1. Update basic data
        ContentValues contentValuesShoppingLists = new ContentValues();

        // content values for basic SL table
        contentValuesShoppingLists.put("name", name);
        contentValuesShoppingLists.put("description", description);

        String whereClause = "shopping_list_id = ?";
        String[] whereArgs = new String[]{String.valueOf(shoppingListId)};

        // use the update method to update sql
        db.update("Shopping_lists", contentValuesShoppingLists, whereClause, whereArgs);


        // 2. Update the SLSI table and ingredients table

        // Delete old data first
        String del1 = "DELETE FROM Shopping_list_supermarket_ingredients WHERE shopping_list_id = ?;";
        db.execSQL(del1, new String[]{String.valueOf(shoppingListId)});

        String del2 = "DELETE FROM Ingredients WHERE ingredient_id NOT IN (SELECT ingredient_id FROM Shopping_list_supermarket_ingredients);";
        db.execSQL(del2);

        ContentValues contentValuesShoppingListSupermarketIngredients = new ContentValues();
        ContentValues contentValuesIngredients = new ContentValues();

        for (Map.Entry<String, ArrayList<ShoppingListIngredient>> entry : shoppingListIngredientsHashMap.entrySet()) {
            String key = entry.getKey();
            ArrayList<ShoppingListIngredient> value = entry.getValue();

            for (ShoppingListIngredient ingredient : value) {
                contentValuesIngredients.put("name", ingredient.getIngredient());
                contentValuesIngredients.put("amount", ingredient.getAmount());
                contentValuesIngredients.put("cost", ingredient.getCost());
                contentValuesIngredients.put("checked", ingredient.isChecked());

                // insert into ingredients table
                long resultIngredients = db.insert("Ingredients", null, contentValuesIngredients);
                if (resultIngredients == -1){
                    return false;
                }

                // insert into supermarket ingredients table
                contentValuesShoppingListSupermarketIngredients.put("shopping_list_id", shoppingListId);
                contentValuesShoppingListSupermarketIngredients.put("ingredient_id", resultIngredients);
                contentValuesShoppingListSupermarketIngredients.put("supermarket", key);

                // insert into supermarket ingredients table
                long resultSupermarketIngredients = db.insert("Shopping_list_supermarket_ingredients", null, contentValuesShoppingListSupermarketIngredients);
                if (resultSupermarketIngredients == -1){
                    return false;
                }
            }
        }
        return true;
    }

    public Cursor getShoppingListsCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        String queryShoppingListsCount = "SELECT COUNT(*) FROM Shopping_lists;";
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryShoppingListsCount, null);
        }
        return cursor;
    }

    // method to mark shoppingList favourited or unfavourited.
    public void updateShoppingListFavourite(int shoppingListId){
        SQLiteDatabase db = this.getWritableDatabase();

        // mark shoppingList as fav
        String update = "UPDATE Shopping_lists SET is_favourited = TRUE WHERE shopping_list_id = ?;";
        db.execSQL(update, new String[]{String.valueOf(shoppingListId)});
    }

    public void updateShoppingListUnFavourite(int shoppingListId){
        SQLiteDatabase db = this.getWritableDatabase();

        String update = "UPDATE Shopping_lists SET is_favourited = FALSE WHERE shopping_list_id = ?;";
        db.execSQL(update, new String[]{String.valueOf(shoppingListId)});
    }


    // method to get data
    public Cursor getShoppingListFromId(int shoppingListId){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT name, description " +
                "FROM Shopping_lists WHERE shopping_list_id = ?;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{String.valueOf(shoppingListId)});
        }
        return cursor;
    }


    public Cursor getShoppingListSupermarketIngredientsFromId(int shoppingListId){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SLSI.supermarket, I.name, I.amount, I.cost, I.checked " +
                "FROM Shopping_lists SL "+
                "JOIN Shopping_list_supermarket_ingredients SLSI ON SL.shopping_list_id = SLSI.shopping_list_id "+
                "JOIN Ingredients I ON SLSI.ingredient_id = I.ingredient_id " +
                "WHERE SL.shopping_list_id = ? " +
                "ORDER BY I.checked ASC, I.name ASC;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{String.valueOf(shoppingListId)});
        }
        return cursor;
    }

    public void deleteShoppingListFromId(int shoppingListId){
        SQLiteDatabase db = this.getWritableDatabase();

        String del1 = "DELETE FROM Shopping_list_supermarket_ingredients WHERE shopping_list_id = ?;";
        db.execSQL(del1, new String[]{String.valueOf(shoppingListId)});

        String del2 = "DELETE FROM Ingredients WHERE ingredient_id NOT IN (SELECT ingredient_id FROM Shopping_list_supermarket_ingredients);";
        db.execSQL(del2);

        String del3 = "DELETE FROM Shopping_lists WHERE shopping_list_id = ?;";
        db.execSQL(del3, new String[]{String.valueOf(shoppingListId)});

    }

    // return a single number (The percentage that the shopping list is completed)
    public Cursor getShoppingListPercentage(int shoppingListId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Extract all SL data
        String queryShoppingList = "SELECT 1.0*SUM(I.checked*I.amount) / SUM(I.amount) " +
                "FROM Shopping_lists SL "+
                "JOIN Shopping_list_supermarket_ingredients SLSI ON SL.shopping_list_id = SLSI.shopping_list_id "+
                "JOIN Ingredients I ON SLSI.ingredient_id = I.ingredient_id " +
                "WHERE SL.shopping_list_id = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryShoppingList, new String[]{String.valueOf(shoppingListId)});
        }
        return cursor;
    }

    // get all supermarkets distinct.
    public Cursor getDistinctSupermarkets(){
        SQLiteDatabase db = this.getReadableDatabase();

        // Extract all SL data
        String queryShoppingList = "SELECT DISTINCT(supermarket) " +
                "FROM Shopping_list_supermarket_ingredients;";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(queryShoppingList, new String[]{});
        }
        return cursor;
    }
}
