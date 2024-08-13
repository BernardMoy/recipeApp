package com.example.recipeapp;

public class Ingredient {
    private String ingredientName;
    private String shopName;
    private float cost;

    public Ingredient(String ingredientName, String shopName, float cost){
        this.ingredientName = ingredientName;
        this.shopName = shopName;
        this.cost = cost;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setCost(float cost){
        this.cost = cost;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public String getShopName() {
        return shopName;
    }

    public float getCost(){
        return cost;
    }
}
