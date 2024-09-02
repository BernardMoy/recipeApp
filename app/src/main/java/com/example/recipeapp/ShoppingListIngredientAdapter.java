package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShoppingListIngredientAdapter extends RecyclerView.Adapter<ShoppingListIngredientRecyclerViewHolder> {

    // display a list of shopping list ingredients -- that belongs to the same supermarket.
    private Context ctx;
    private ArrayList<ShoppingListIngredient> shoppingListIngredientList;

    public ShoppingListIngredientAdapter(Context ctx, ArrayList<ShoppingListIngredient> shoppingListIngredientList){
        this.ctx = ctx;
        this.shoppingListIngredientList = shoppingListIngredientList;
    }

    @NonNull
    @Override
    public ShoppingListIngredientRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingListIngredientRecyclerViewHolder(LayoutInflater.from(ctx).inflate(R.layout.shopping_list_ingredient_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListIngredientRecyclerViewHolder holder, int position) {
        ShoppingListIngredient ingredient = shoppingListIngredientList.get(position);

        holder.getIngredientTextView().setText(ingredient.getIngredient());
        holder.getAmountTextView().setText(String.valueOf(ingredient.getAmount()));
        holder.getCostTextView().setText(String.valueOf(ingredient.getCost()));
    }

    @Override
    public int getItemCount() {
        return shoppingListIngredientList.size();
    }
}
