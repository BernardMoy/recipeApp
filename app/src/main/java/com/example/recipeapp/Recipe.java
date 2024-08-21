package com.example.recipeapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Recipe {

    // for displaying recipes in an activity for displaying it.

    private String name;
    private byte[] image;
    private String description;
    private String link;
    private float prepTime;
    private boolean isFavourited;
    private int timesCooked;

    private ArrayList<String> tagList;
    private ArrayList<Ingredient> ingredientList;

    public Recipe(String name, byte[] image, String description, String link, float prepTime,
                  boolean isFavourited, int timesCooked,
                  ArrayList<String> tagList, ArrayList<Ingredient> ingredientList){
        this.name = name;
        this.image = image;
        this.description = description;
        this.link = link;
        this.prepTime = prepTime;
        this.tagList = tagList;
        this.ingredientList = ingredientList;
        this.isFavourited = isFavourited;
        this.timesCooked = timesCooked;
    }

    public String getName() {
        return name;
    }

    public byte[] getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public float getPrepTime() {
        return prepTime;
    }

    public boolean isFavourited() {
        return isFavourited;
    }

    public int getTimesCooked() {
        return timesCooked;
    }

    public ArrayList<String> getTagList() {
        return tagList;
    }

    public ArrayList<Ingredient> getIngredientList() {
        return ingredientList;
    }

    // calculate the total weighed cost
    public float calculateCost(){
        float total = 0.0f;
        for (Ingredient i : ingredientList){
            total += i.getCost() * i.getAmount();
        }

        return total;
    }
}
