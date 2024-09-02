package com.example.recipeapp;

// same as ingredient, except there are no SUPERMARKET column and amount is INT
// It is separately stored in the hashmap and db.

public class ShoppingListIngredient {
    private String ingredientName;
    private int amount;
    private float cost;

    public ShoppingListIngredient(String ingredientName, int amount, float cost){
        this.ingredientName = ingredientName;
        this.amount = amount;
        this.cost = cost;
    }

    public String getIngredient() {
        return ingredientName;
    }

    public int getAmount() {
        return amount;
    }

    public float getCost() {
        return cost;
    }
}
