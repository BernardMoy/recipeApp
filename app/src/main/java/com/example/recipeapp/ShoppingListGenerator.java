package com.example.recipeapp;

import android.content.Context;
import android.database.Cursor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import kotlin.Triple;

public class ShoppingListGenerator {

    private Context ctx;
    private HashMap<Integer, Integer> selectedRecipeIdMap;   // store the hashmap of key = recipeId, value = count

    public ShoppingListGenerator(Context ctx, HashMap<Integer, Integer> selectedRecipeIdMap){
        this.ctx = ctx;
        this.selectedRecipeIdMap = selectedRecipeIdMap;
    }

    /**
     * method to generate shopping list ingredients from a set of recipe ids.
     * 2 ingredients are considered identical if their NAME (CASE SENSITIVE), COST and SUPERMARKET are the same.
     * Hence, (supermarket, name, cost) uniquely identifies an ingredient.
     * It is stored before elements are added to the final hashMap.
     *
     * @return True if there are ingredients to be added (And successfully added), False otherwise
     */
    public boolean generateShoppingListFromRecipeIds(String shoppingListName){

        // 1. add all ingredients to this hashset to uniquely identify them first
        // key = triple, value = amount
        HashMap<Triple<String, String, Float>, Float> uniqueIngredientsMap = new HashMap<>();
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);

        // extract information from each recipe id and put them into hashmap
        String name;
        Float amount;
        String supermarket;
        Float cost;

        for (Map.Entry<Integer, Integer> entry : selectedRecipeIdMap.entrySet()){
            int recipeId = entry.getKey();
            int count = entry.getValue();

            Cursor cursor = db.getIngredientsFromId(recipeId);
            if (cursor.getCount() > 0){
                while (cursor.moveToNext()){
                    name = cursor.getString(0);
                    amount = cursor.getFloat(1)*count;
                    supermarket = cursor.getString(2);
                    cost = cursor.getFloat(3);

                    // add triples to hashset -- the real amount is amount*count
                    Triple<String, String, Float> triple = new Triple<>(supermarket, name, cost);
                    if (uniqueIngredientsMap.containsKey(triple)){
                        Float originalAmount = uniqueIngredientsMap.get(triple);
                        uniqueIngredientsMap.put(triple, originalAmount + amount);

                    } else {
                        uniqueIngredientsMap.put(triple, amount);
                    }

                }
            }
        }

        // 2. Create the LinkedHashMap from the set of ingredient triples
        LinkedHashMap<String, ArrayList<ShoppingListIngredient>> shoppingListIngredientMap = new LinkedHashMap<>();

        for (Map.Entry<Triple<String, String, Float>, Float> entry : uniqueIngredientsMap.entrySet()){
            Triple<String, String, Float> key = entry.getKey();
            String finalSupermarket = key.getFirst();
            String finalIngredientName = key.getSecond();
            Float finalCost = key.getThird();

            Float totalAmount = entry.getValue();

            // round up the total amount
            int totalAmountRoundedUp = (int) Math.ceil(totalAmount);

            // create ShoppingListIngredient
            ShoppingListIngredient shoppingListIngredient = new ShoppingListIngredient(finalIngredientName, totalAmountRoundedUp, finalCost, false);

            // add the ShoppingListIngredient to the linked hash map
            if (shoppingListIngredientMap.containsKey(finalSupermarket)){
                shoppingListIngredientMap.get(finalSupermarket).add(shoppingListIngredient);

            } else {
                ArrayList<ShoppingListIngredient> arrayList = new ArrayList<>();
                arrayList.add(shoppingListIngredient);
                shoppingListIngredientMap.put(finalSupermarket, arrayList);

            }
        }

        // 3. Add the information to the db, if the shopping list ingredient map is not empty
        if (!shoppingListIngredientMap.isEmpty()){
            DatabaseHelperShoppingLists dbs = new DatabaseHelperShoppingLists(ctx);
            dbs.addShoppingList(shoppingListName, "", shoppingListIngredientMap);
            return true;
        }

        return false;
    }
}
