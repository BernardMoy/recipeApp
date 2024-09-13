package com.example.recipeapp;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.util.Pair;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kotlin.Triple;

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

        HashMap<FourItems<String, String, Float, Integer>, Pair<String, Float>> map = getAllUnexpiredIngredients();

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
    public float calculateCostSavedFromRecipe(int recipeId, HashMap<FourItems<String, String, Float, Integer>, Pair<String, Float>> map){
        float amountSaved = 0.0f;

        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);
        Cursor cursor = db.getIngredientsFromId(recipeId);
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String name = cursor.getString(0);
                float amount = cursor.getFloat(1);
                String supermarket = cursor.getString(2);
                float cost = cursor.getFloat(3);
                int shelfLife = cursor.getInt(4);

                FourItems<String, String, Float, Integer> currentIngredient = new FourItems<>(name, supermarket, cost, shelfLife);

                // get the min amount of this amount and the leftover amount

                if (map.containsKey(currentIngredient)){
                    float minAmount = Math.min(amount, map.get(currentIngredient).second);
                    amountSaved += minAmount*cost;
                }
            }
        }
        db.close();

        return amountSaved;
    }

    /**
     * get all ingredients that haven't expired till the current date.
     * When adding ingredient to hashmap, if it is already in the hashmap with current amount n and required amount m:
     * if m<n, then n -= m
     * else, m = ceil(m-n) - (m-n) and update date to new current date -- (m-n) is the amount that is used after deducting remaining ing
     *
     * @return A hashmap where
     * key = (name, supermarket, cost, shelfLife) which identifies an ingredient
     * value = (date Last used, amount leftover)
     */
    public HashMap<FourItems<String, String, Float, Integer>, Pair<String, Float>> getAllUnexpiredIngredients(){
        HashMap<FourItems<String, String, Float, Integer>, Pair<String, Float>> map = new HashMap<>();

        // iterate every meal.
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);
        Cursor cursor = db.getIngredientsFromMealTillDate(currentDate);
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                // only add to arraylist if the ingredient HAS NOT EXPIRED.
                String name = cursor.getString(1);
                float amount = cursor.getFloat(2);
                String supermarket = cursor.getString(3);
                Float cost = cursor.getFloat(4);
                int shelfLife = cursor.getInt(5);
                String mealDate = cursor.getString(6);

                FourItems<String, String, Float, Integer> currentIngredient = new FourItems<>(name, supermarket, cost, shelfLife);

                if (map.containsKey(currentIngredient)){
                    // if the current ingredient is already in the map
                    Pair<String, Float> pair = map.get(currentIngredient);
                    String lastUsedDate = pair.first;
                    float remainingAmount = pair.second;

                    Log.d("dates", lastUsedDate + mealDate);

                    // compare the meal date to the expiry date of the ingredient
                    if (differenceOfTwoDates(lastUsedDate, mealDate) <= shelfLife){

                        Pair<String, Float> newPair;

                        // use the remaining ingredient as you can
                        if (amount <= remainingAmount){
                            newPair = new Pair<>(lastUsedDate, remainingAmount - amount);

                        } else {
                            // update the date to the meal date
                            newPair = new Pair<>(mealDate, (float)(Math.ceil(amount-remainingAmount)-(amount-remainingAmount)));
                        }

                        // update the newPair into the map
                        map.put(currentIngredient, newPair);

                    } else {
                        // Remove that ingredient! It cannot be used anymore
                        Pair<String, Float> newPair = new Pair<>(mealDate, (float)(Math.ceil(amount)-amount));
                        map.put(currentIngredient, newPair);
                    }

                } else {
                    Pair<String, Float> newPair = new Pair<>(mealDate, (float)(Math.ceil(amount)-amount));
                    map.put(currentIngredient, newPair);
                }
            }
        }
        db.close();

        // in the map, remove all ingredients that are expired with respect to CURRENT DATE
        ArrayList<FourItems<String, String, Float, Integer>> toBeRemovedList = new ArrayList<>();

        for (Map.Entry<FourItems<String, String, Float, Integer>, Pair<String, Float>> entry : map.entrySet()){
            FourItems<String, String, Float, Integer> key = entry.getKey();
            Pair<String, Float> value = entry.getValue();

            if (differenceOfTwoDates(value.first, currentDate) > key.fourth){
                toBeRemovedList.add(key);
            }
        }

        for (FourItems<String, String, Float, Integer> entry : toBeRemovedList){
            map.remove(entry);
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
