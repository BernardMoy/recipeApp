package com.example.recipeapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Recipe {
    private String recipeName;
    private ArrayList<String> tags;
    private String description;
    private int prepTime;
    private HashMap<Ingredient, Float> requiredIngredients;
    private HashMap<Ingredient, Float> optionalIngredients;
    private String recipeLink;
    private int recipeImage;
    private boolean favourite;
    private int timesCooked;

    public Recipe(String recipeName, ArrayList<String> tags, String description, int prepTime,
                  HashMap<Ingredient, Float> requiredIngredients, HashMap<Ingredient, Float> optionalIngredients,
                  String recipeLink, int recipeImage){
        this.recipeName = recipeName;
        this.tags = tags;
        this.description = description;
        this.prepTime = prepTime;
        this.requiredIngredients = requiredIngredients;
        this.optionalIngredients = optionalIngredients;
        this.recipeLink = recipeLink;
        this.recipeImage = recipeImage;
        this.favourite = false;
        this.timesCooked = 0;
    }

    public void markFavourite(){
        this.favourite = true;
    }
    public void markUnFavourite(){
        this.favourite = false;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOptionalIngredients(HashMap<Ingredient, Float> optionalIngredients) {
        this.optionalIngredients = optionalIngredients;
    }

    public void setRequiredIngredients(HashMap<Ingredient, Float> requiredIngredients) {
        this.requiredIngredients = requiredIngredients;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public void setRecipeImage(int recipeImage) {
        this.recipeImage = recipeImage;
    }

    public void setRecipeLink(String recipeLink) {
        this.recipeLink = recipeLink;
    }

    public void incrementTimesCooked(){
        timesCooked++;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public int getRecipeImage() {
        return recipeImage;
    }

    public String getDescription() {
        return description;
    }

    public String getRecipeLink() {
        return recipeLink;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public HashMap<Ingredient, Float> getOptionalIngredients() {
        return optionalIngredients;
    }

    public HashMap<Ingredient, Float> getRequiredIngredients() {
        return requiredIngredients;
    }
    public boolean getFavourite(){
        return this.favourite;
    }

    public int getTimesCooked() {
        return timesCooked;
    }

    public float calculateCost(){
        float cost = 0;
        for (Map.Entry<Ingredient, Float> entry : requiredIngredients.entrySet()){
            cost += entry.getKey().getCost()*entry.getValue();
        }
        return cost;
    }
}
