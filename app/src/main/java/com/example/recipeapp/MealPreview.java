package com.example.recipeapp;

public class MealPreview {

    private int mealId;
    private String category;
    private Byte[] recipeImage;
    private String recipeName;
    private int recipeCost;

    public MealPreview(int mealId, String category, Byte[] recipeImage, String recipeName, int recipeCost){
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

    public Byte[] getRecipeImage() {
        return recipeImage;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public int getRecipeCost() {
        return recipeCost;
    }
}
