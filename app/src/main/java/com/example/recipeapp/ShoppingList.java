package com.example.recipeapp;

import java.util.ArrayList;
public class ShoppingList {
    private String listName;
    private ArrayList<ShoppingListIngredient> listIngredients;

    public ShoppingList(String listName){
        this.listName = listName;
        this.listIngredients = new ArrayList<>();
    }

    public void setListIngredients(ArrayList<ShoppingListIngredient> listIngredients) {
        this.listIngredients = listIngredients;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListName() {
        return listName;
    }

    // for ingredients in a shopping list -- their amount cannot be less than 1
    public ArrayList<ShoppingListIngredient> getListIngredients() {
        return listIngredients;
    }

    public int getCount(){
        return listIngredients.size();
    }

    public float getTotalCost(){
        float totalCost = 0.0f;
        for (int i = 0 ; i < listIngredients.size(); i++){
            ShoppingListIngredient ingredient = listIngredients.get(i);
            totalCost += ingredient.getCost() * ingredient.getAmount();
        }
        return totalCost;
    }
}
