package com.example.recipeapp;

public class Ingredient {
    private String ingredientName;
    private float amount;
    private String supermarket;
    private float cost;
    private float shelfLife;

    public Ingredient(String ingredientName, float amount, String supermarket, float cost, float shelfLife){
        this.ingredientName = ingredientName;
        this.amount = amount;
        this.supermarket = supermarket;
        this.cost = cost;
        this.shelfLife = shelfLife;
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

    public float getShelfLife() {
        return shelfLife;
    }
}
