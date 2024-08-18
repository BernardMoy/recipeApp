package com.example.recipeapp;

public class Ingredient {
    private String ingredientName;
    private float portionSize;
    private String supermarket;
    private float cost;

    public Ingredient(String ingredientName, float portionSize, String supermarket, float cost){
        this.ingredientName = ingredientName;
        this.portionSize = portionSize;
        this.supermarket = supermarket;
        this.cost = cost;
    }

    public String getIngredient() {
        return ingredientName;
    }

    public float getPortionSize() {
        return portionSize;
    }

    public String getSupermarket() {
        return supermarket;
    }

    public float getCost() {
        return cost;
    }
}
