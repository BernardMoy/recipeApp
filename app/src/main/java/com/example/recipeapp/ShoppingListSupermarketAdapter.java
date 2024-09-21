package com.example.recipeapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class ShoppingListSupermarketAdapter extends RecyclerView.Adapter<ShoppingListSupermarketRecyclerViewHolder>{

    // variables
    private Context ctx;
    private LinkedHashMap<String, ArrayList<ShoppingListIngredient>> shoppingListIngredientsHashMap;
    // stores the list of keys in the above hashmap in an arraylist
    private ArrayList<String> keys;

    public ShoppingListSupermarketAdapter(Context ctx, LinkedHashMap<String, ArrayList<ShoppingListIngredient>> shoppingListIngredientsHashMap){
        this.ctx = ctx;
        this.shoppingListIngredientsHashMap = shoppingListIngredientsHashMap;
    }

    @NonNull
    @Override
    public ShoppingListSupermarketRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingListSupermarketRecyclerViewHolder(LayoutInflater.from(ctx).inflate(R.layout.shopping_list_supermarket_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListSupermarketRecyclerViewHolder holder, int position) {
        // Extract supermarket info from the linked hash map
        keys = new ArrayList<>(shoppingListIngredientsHashMap.keySet());

        String supermarketName = keys.get(position);
        ArrayList<ShoppingListIngredient> ingredientList = shoppingListIngredientsHashMap.get(supermarketName);

        holder.getSupermarketNameTextView().setText(supermarketName);


        // modify the supermarket count textview and the supermarket total cost textview
        int totalAmount = 0;
        for (ShoppingListIngredient i : ingredientList){
            totalAmount += i.getAmount();
        }
        String countText = String.valueOf(totalAmount) + " items";
        holder.getSupermarketCountTextView().setText(countText);

        // get the total cost of ingredients
        float totalCost = 0.0f;
        for (ShoppingListIngredient i : ingredientList){
            totalCost += i.getCost()*i.getAmount();
        }
        String costText = String.format(Locale.getDefault(), "%.2f", totalCost);
        holder.getSupermarketTotalCostTextView().setText(costText);


        // use the ingredientsList to populate the inner ShoppingListIngredientReyclerView
        // modify the recycler view that displays list of items
        RecyclerView ingredientsRecyclerView = holder.getShoppingListIngredientsRecyclerView();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        ingredientsRecyclerView.setLayoutManager(linearLayoutManager);


        /*
        This listener is called when an ingredient is deleted.

        A callback is made to the parent to update the displayed count and total cost.
        If there are no more ingredient, then delete it.
        */
        ShoppingListIngredientAdapter ingredientAdapter = new ShoppingListIngredientAdapter(ctx, ingredientList, new OnIngredientChangeListener() {
            @Override
            public void updateCountAndCost(int newCount, float newTotalCost) {
                // update the count and cost displayed
                // if new count = 0, remove the entire supermarket instead
                if (newCount == 0){
                    int delPos = holder.getAdapterPosition();
                    // find the key associated with the current position
                    String keyToBeRemoved = keys.get(delPos);

                    // update the hashMap
                    shoppingListIngredientsHashMap.remove(keyToBeRemoved);

                    // update the keys
                    keys.remove(delPos);

                    notifyItemRemoved(delPos);


                } else {
                    String countText = String.valueOf(newCount) + " items";
                    String costText = String.format(Locale.getDefault(), "%.2f", newTotalCost);

                    holder.getSupermarketCountTextView().setText(countText);
                    holder.getSupermarketTotalCostTextView().setText(costText);

                }
            }
        });
        ingredientsRecyclerView.setAdapter(ingredientAdapter);
    }

    @Override
    public int getItemCount() {
        return shoppingListIngredientsHashMap.size();
    }
}
