package com.example.recipeapp;

import android.content.Context;
import android.database.Cursor;
import android.util.Pair;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeSuggester {
    private String currentDate;
    private Context ctx;

    public RecipeSuggester(Context ctx, String currentDate){
        this.ctx = ctx;
        this.currentDate = currentDate;
    }

    /**
     * Suggest a recipe that saves the most cost in utilising the remaining unexpired ingredients.
     *
     * @return A pair, first item = the recipeID, second item = the cost that is saved
     */
    public Pair<Integer, Float> suggestRecipe(){

        HashMap<Integer, Float> map = getAllUnexpiredIngredients();

        float currentAmountSaved = 0.0f;
        int currentRecipeId = -1;

        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);
        // get all recipe ids
        Cursor cursor = db.getRecipes(0);
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                int recipeId = cursor.getInt(0);
                float amountSaved = calculateCostSavedFromRecipe(recipeId, map);

                // update current max values
                if (amountSaved > currentAmountSaved){
                    currentAmountSaved = amountSaved;
                    currentRecipeId = recipeId;
                }

            }
        }

        return new Pair<>(currentRecipeId, currentAmountSaved);
    }

    /**
     * Given a recipeId and a map of (ingID: count), calculate the cost that can be saved by adding this recipe.
     * @param recipeId recipeID to be iterated
     * @param map map of type (ingID: count)
     * @return the cost that is saved.
     */
    public float calculateCostSavedFromRecipe(int recipeId, HashMap<Integer, Float> map){
        float amountSaved = 0.0f;

        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);
        Cursor cursor = db.getIngredientsFromId(recipeId);
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                int ingredientId = cursor.getInt(5);
                float amount = cursor.getFloat(1);
                float cost = cursor.getFloat(3);

                // get the min amount of this amount and the leftover amount
                if (map.containsKey(ingredientId)){
                    float minAmount = Math.min(amount, map.get(ingredientId));
                    amountSaved += minAmount*cost;
                }
            }
        }

        return amountSaved;
    }

    /**
     * get all ingredients that haven't expired till the current date.
     *
     * @return A hashmap key = ingredientID, value = the LEFTOVER amount of it
     */
    public HashMap<Integer, Float> getAllUnexpiredIngredients(){
        HashMap<Integer, Float> map = new HashMap<>();

        // iterate every meal.
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);
        Cursor cursor = db.getIngredientsFromMealTillDate(currentDate);
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                // only add to arraylist if the ingredient HAS NOT EXPIRED.
                int ingredientId = cursor.getInt(0);
                String name = cursor.getString(1);
                float amount = cursor.getFloat(2);
                int shelfLife = cursor.getInt(5);
                String mealDate = cursor.getString(6);

                if (differenceOfTwoDates(mealDate, currentDate) <= shelfLife){
                    if (map.containsKey(ingredientId)){
                        float originalCount = map.get(ingredientId);
                        map.put(ingredientId, originalCount + amount);

                    } else {
                        map.put(ingredientId, amount);
                    }
                }
            }
        }

        // for each item in the map, convert their value to the LEFTOVER ingredient value
        for (Map.Entry<Integer, Float> entry : map.entrySet()){
            int key = entry.getKey();
            float value = entry.getValue();

            float newValue = (float)(Math.ceil(value) - value);

            // update value
            if (newValue == 0){
                map.remove(key);

            } else {
                map.put(key, newValue);

            }
        }

        return map;
    }

    // get the difference between 2 dates
    public long differenceOfTwoDates(String ds1, String ds2){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate firstDate = LocalDate.parse(ds1, formatter);
        LocalDate secondDate = LocalDate.parse(ds2, formatter);

        return ChronoUnit.DAYS.between(firstDate, secondDate);
    }
}
