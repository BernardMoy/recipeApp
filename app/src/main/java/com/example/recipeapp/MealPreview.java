package com.example.recipeapp;

public class MealPreview {

    private int mealId;
    private String category;
    private byte[] recipeImage;
    private String recipeName;
    private float recipeCost;

    public MealPreview(int mealId, String category, byte[] recipeImage, String recipeName, float recipeCost){
        this.mealId = mealId;
        this.category = category;
        this.recipeImage = recipeImage;
        this.recipeName = recipeName;
        this.recipeCost = recipeCost;
    }

    public int getMealId() {
        return mealId;
    }

    public String getCategory() {
        return category;
    }

    public byte[] getRecipeImage() {
        return recipeImage;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public float getRecipeCost() {
        return recipeCost;
    }
}
