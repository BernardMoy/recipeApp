package com.example.recipeapp;

public class MealRecipeSuggestionPreview {

    private byte[] recipeImage;
    private String recipeName;
    private Float recipeCost;

    public MealRecipeSuggestionPreview(byte[] recipeImage, String recipeName, Float recipeCost){
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
}
