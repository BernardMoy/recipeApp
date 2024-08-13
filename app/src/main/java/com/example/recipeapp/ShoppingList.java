package com.example.recipeapp;

import java.util.ArrayList;
public class ShoppingList {
    private String listName;
    private ArrayList<Ingredient> listIngredients;

    public ShoppingList(String listName){
        this.listName = listName;
        this.listIngredients = new ArrayList<>();
    }

    public void setListIngredients(ArrayList<Ingredient> listIngredients) {
        this.listIngredients = listIngredients;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListName() {
        return listName;
    }

    public ArrayList<Ingredient> getListIngredients() {
        return listIngredients;
    }

    public int getCount(){
        return listIngredients.size();
    }

    public float getTotalCost(){
        float totalCost = 0;
        for (int i = 0 ; i < listIngredients.size(); i++){
            totalCost += listIngredients.get(i).getCost();
        }
        return totalCost;
    }
}
