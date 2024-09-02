package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShoppingListSupermarketAdapter extends RecyclerView.Adapter<ShoppingListSupermarketRecyclerViewHolder> {

    // variables
    private Context ctx;
    private HashMap<String, ArrayList<ShoppingListIngredient>> shoppingListIngredientsHashMap;


    public ShoppingListSupermarketAdapter(Context ctx, HashMap<String, ArrayList<ShoppingListIngredient>> shoppingListIngredientsHashMap){
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
        List<String> keys = List.copyOf(shoppingListIngredientsHashMap.keySet());
        String supermarketName = keys.get(position);
        ArrayList<ShoppingListIngredient> ingredientList = shoppingListIngredientsHashMap.get(supermarketName);

        holder.getSupermarketNameTextView().setText(supermarketName);

        // use the ingredientsList to populate the inner ShoppingListIngredientReyclerView
        // modify the recycler view that displays list of items
        RecyclerView ingredientsRecyclerView = holder.getShoppingListIngredientsRecyclerView();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        ingredientsRecyclerView.setLayoutManager(linearLayoutManager);

        ShoppingListIngredientAdapter ingredientAdapter = new ShoppingListIngredientAdapter(ctx, ingredientList);
        ingredientsRecyclerView.setAdapter(ingredientAdapter);
    }

    @Override
    public int getItemCount() {
        return shoppingListIngredientsHashMap.size();
    }
}
