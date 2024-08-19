package com.example.recipeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper{

    private Context context;
    private static final String DATABASE_NAME = "recipeApp.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the database schema
        String createRecipes =
                "CREATE TABLE Recipes (" +
                "recipe_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(255) NOT NULL, " +
                "image BLOB, " +
                "description TEXT," +
                "link TEXT," +
                "prep_time FLOAT,"+
                "is_favourited BOOLEAN DEFAULT FALSE," +
                "times_cooked INTEGER DEFAULT 0" +
                ");";
        db.execSQL(createRecipes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Recipes;");
    }

    // reset database
    public void reset(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Recipes;");
        onCreate(db);     // recreate the database
    }

    // method to add new recipe
    public long addRecipe(String name, byte[] image, String description, String link, float prepTime, List<String> tagList, List<Ingredient> ingredientList){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // set new values submitted
        contentValues.put("name", name);
        contentValues.put("image", image);
        contentValues.put("description", description);
        contentValues.put("link", link);
        contentValues.put("prep_time", prepTime);

        long result = db.insert("Recipes", null, contentValues);
        return result;
    }
}
