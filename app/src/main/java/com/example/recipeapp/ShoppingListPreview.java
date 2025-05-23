package com.example.recipeapp;

public class ShoppingListPreview {

    private int shoppingListId;
    private String name;
    private int itemCount;
    private int supermarketCount;
    private float cost;
    private boolean isFavourited;

    public ShoppingListPreview(int shoppingListId, String name, int itemCount, int supermarketCount, float cost, boolean isFavourited){
        this.shoppingListId = shoppingListId;
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
        return Math.round(cost*100.0f)/100.0f;
    }

    public boolean isFavourited() {
        return isFavourited;
    }

    public void setIsFavourited(boolean b){
        this.isFavourited = b;
    }

    public int getShoppingListId() {
        return shoppingListId;
    }
}
