package com.example.recipeapp;

public class IngredientSuggestion {
    private String ingredientName;
    private String supermarket;
    private float cost;
    private int shelfLife;

    public IngredientSuggestion(String ingredientName, String supermarket, float cost, int shelfLife){
        this.ingredientName = ingredientName;
        this.supermarket = supermarket;
        this.cost = cost;
        this.shelfLife = shelfLife;
    }

    public String getIngredient() {
        return ingredientName;
    }

    public String getSupermarket() {
        return supermarket;
    }

    public float getCost() {
        return cost;
    }

    public int getShelfLife() {
        return shelfLife;
    }
}