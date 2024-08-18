package com.example.recipeapp;

public class Ingredient {
    private String ingredientName;
    private float amount;
    private String supermarket;
    private float cost;

    public Ingredient(String ingredientName, float amount, String supermarket, float cost){
        this.ingredientName = ingredientName;
        this.amount = amount;
        this.supermarket = supermarket;
        this.cost = cost;
    }

    public String getIngredient() {
        return ingredientName;
    }

    public float getAmount() {
        return amount;
    }

    public String getSupermarket() {
        return supermarket;
    }

    public float getCost() {
        return cost;
    }
}
