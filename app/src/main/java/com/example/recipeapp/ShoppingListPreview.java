package com.example.recipeapp;

public class ShoppingListPreview {

    private String name;
    private int itemCount;
    private int supermarketCount;
    private float cost;
    private boolean isFavourited;

    public ShoppingListPreview(String name, int itemCount, int supermarketCount, float cost, boolean isFavourited){
        this.name = name;
        this.itemCount = itemCount;
        this.supermarketCount = supermarketCount;
        this.cost = cost;
        this.isFavourited = isFavourited;
    }


    public String getName() {
        return name;
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getSupermarketCount() {
        return supermarketCount;
    }

    public float getCost() {
        return cost;
    }

    public boolean isFavourited() {
        return isFavourited;
    }
}
