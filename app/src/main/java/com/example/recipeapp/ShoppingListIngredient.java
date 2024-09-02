package com.example.recipeapp;

// same as ingredient, except there are no SUPERMARKET column
// It is separately stored in the hashmap and db.

public class ShoppingListIngredient {
    private String ingredientName;
    private float amount;
    private float cost;

    public ShoppingListIngredient(String ingredientName, float amount, float cost){
        this.ingredientName = ingredientName;
        this.amount = amount;
        this.cost = cost;
    }

    public String getIngredient() {
        return ingredientName;
    }

    public float getAmount() {
        return amount;
    }

    public float getCost() {
        return cost;
    }
}
