package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class ShoppingListIngredientAdapter extends RecyclerView.Adapter<ShoppingListIngredientRecyclerViewHolder> {

    // display a list of shopping list ingredients -- that belongs to the same supermarket.
    private Context ctx;
    private ArrayList<ShoppingListIngredient> shoppingListIngredientList;
    private OnIngredientChangeListener listener;

    public ShoppingListIngredientAdapter(Context ctx, ArrayList<ShoppingListIngredient> shoppingListIngredientList, OnIngredientChangeListener listener){
        this.ctx = ctx;
        this.shoppingListIngredientList = shoppingListIngredientList;
        this.listener = listener;
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

        // set alternating row colours
        if (position % 2 == 0){
            holder.getTableRow().setBackgroundResource(R.color.lightColor);
        }

        // set up delete button functionality
        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos != -1){   // prevent clicking too fast
                    shoppingListIngredientList.remove(pos);
                    notifyItemRemoved(pos);  // update the displayed view

                    // get the total cost of ingredients
                    float totalCost = 0.0f;
                    for (ShoppingListIngredient i : shoppingListIngredientList){
                        totalCost += i.getCost()*i.getAmount();
                    }

                    // call the listener to update parent recyclerview data
                    listener.updateCountAndCost(shoppingListIngredientList.size(), totalCost);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingListIngredientList.size();
    }
}
