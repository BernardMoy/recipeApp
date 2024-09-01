package com.example.recipeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
                        "amount FLOAT CHECK(amount >= 1), " +
                        "cost FLOAT CHECK(cost >= 0)" +
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
        Toast.makeText(context, "Database reset", Toast.LENGTH_SHORT).show();
    }
}
