package com.example.recipeapp;

public class MealRecipeSuggestionPreview {

    private int recipeId;
    private byte[] recipeImage;
    private String recipeName;
    private Float recipeCost;

    public MealRecipeSuggestionPreview(int recipeId, byte[] recipeImage, String recipeName, Float recipeCost){
        this.recipeId = recipeId;
        this.recipeImage = recipeImage;
        this.recipeName = recipeName;
        this.recipeCost = recipeCost;
    }

    public byte[] getRecipeImage() {
        return recipeImage;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public Float getRecipeCost() {
        return recipeCost;
    }

    public int getRecipeId() {
        return recipeId;
    }
}
