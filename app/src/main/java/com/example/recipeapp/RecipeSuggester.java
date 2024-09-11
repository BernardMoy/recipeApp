package com.example.recipeapp;

import android.content.Context;
import android.database.Cursor;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class RecipeSuggester {
    private String currentDate;
    private Context ctx;

    public RecipeSuggester(Context ctx, String currentDate){
        this.ctx = ctx;
        this.currentDate = currentDate;
    }

    public int suggestRecipe(){
        return 0;
    }

    // get all ingredients that haven't expired till the current date.
    public ArrayList<Ingredient> getAllUnexpiredIngredients(){
        ArrayList<Ingredient> resultList = new ArrayList<>();

        // iterate every meal.
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);
        Cursor cursor = db.getIngredientsFromMealTillDate(currentDate);
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                // only add to arraylist if the ingredient HAS NOT EXPIRED.
                String name = cursor.getString(0);
                float amount = cursor.getFloat(1);
                String supermarket = cursor.getString(2);
                float cost = cursor.getFloat(3);
                int shelfLife = cursor.getInt(4);
                String mealDate = cursor.getString(5);

                if (differenceOfTwoDates(mealDate, currentDate) <= shelfLife){
                    Ingredient i = new Ingredient(name, amount, supermarket, cost, shelfLife);
                    resultList.add(i);
                }
            }
        }

        return resultList;
    }

    // get the difference between 2 dates
    public long differenceOfTwoDates(String ds1, String ds2){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate firstDate = LocalDate.parse(ds1, formatter);
        LocalDate secondDate = LocalDate.parse(ds2, formatter);

        return ChronoUnit.DAYS.between(firstDate, secondDate);
    }
}
