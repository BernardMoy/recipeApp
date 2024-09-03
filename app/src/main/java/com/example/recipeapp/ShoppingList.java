package com.example.recipeapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ShoppingList {
    private String listName;
    private String description;
    private LinkedHashMap<String, ArrayList<ShoppingListIngredient>> shoppingListIngredientsHashMap;

    public ShoppingList(String listName, String description, LinkedHashMap<String, ArrayList<ShoppingListIngredient>> shoppingListIngredientsHashMap){
        this.listName = listName;
        this.description = description;
        this.shoppingListIngredientsHashMap = new LinkedHashMap<>();
    }

    public int getCount(){
        return shoppingListIngredientsHashMap.size();
    }

    // get total cost of ingredients when given a supermarket string (Used to display them in the add UI).
    public float getTotalCost(String supermarket){
        float totalCost = 0.0f;

        ArrayList<ShoppingListIngredient> listIngredients = shoppingListIngredientsHashMap.get(supermarket);
        for (int i = 0 ; i < listIngredients.size(); i++){
            ShoppingListIngredient ingredient = listIngredients.get(i);
            totalCost += ingredient.getCost() * ingredient.getAmount();
        }
        return totalCost;
    }
}
