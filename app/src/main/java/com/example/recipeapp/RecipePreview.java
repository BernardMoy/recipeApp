package com.example.recipeapp;

public class RecipePreview {

    // for displaying recipes in the main activity (Recyclerview).

    private int recipeId;
    private String name;
    private String tag;
    private int tagPlus;
    private float cost;
    private float prepTime;
    private boolean isFavourited;
    private int timesCooked;
    private byte[] image;


    public RecipePreview(int recipeId, String name, String tag, int tagPlus, float cost, float prepTime,
                         boolean isFavourited, int timesCooked, byte[] image){
        this.recipeId = recipeId;
        this.name = name;
        this.tag = tag;
        this.tagPlus = tagPlus;
        this.cost = cost;
        this.prepTime = prepTime;
        this.isFavourited = isFavourited;
        this.timesCooked = timesCooked;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public int getTagPlus() {
        return tagPlus;
    }

    public float getCost() {
        return cost;
    }

    public float getPrepTime() {
        return prepTime;
    }

    public boolean isFavourited() {
        return isFavourited;
    }

    public void setIsFavourited(Boolean b){
        this.isFavourited = b;
    }

    public int getTimesCooked() {
        return timesCooked;
    }

    public byte[] getImage() {
        return image;
    }

    public int getRecipeId() {
        return recipeId;
    }
}
