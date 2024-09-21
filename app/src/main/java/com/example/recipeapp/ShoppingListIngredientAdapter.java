package com.example.recipeapp;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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



        // discard the prev listener
        holder.getCheckButton().setOnCheckedChangeListener(null);

        // change the displayed icon depending whether is checked
        ShoppingListIngredient currentIngredient = shoppingListIngredientList.get(position);
        if (currentIngredient.isChecked()){
            holder.getCheckButton().setChecked(true);
            holder.getIngredientTextView().setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.getIngredientTextView().setTextColor(ContextCompat.getColor(ctx, R.color.gray));

            holder.getAmountTextView().setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.getAmountTextView().setTextColor(ContextCompat.getColor(ctx, R.color.gray));

            holder.getCostTextView().setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.getCostTextView().setTextColor(ContextCompat.getColor(ctx, R.color.gray));
        }

        // listener for check button (Toggle button)
        holder.getCheckButton().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    holder.getIngredientTextView().setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.getIngredientTextView().setTextColor(ContextCompat.getColor(ctx, R.color.gray));

                    holder.getAmountTextView().setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.getAmountTextView().setTextColor(ContextCompat.getColor(ctx, R.color.gray));

                    holder.getCostTextView().setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.getCostTextView().setTextColor(ContextCompat.getColor(ctx, R.color.gray));

                    // mark the ingredient in the current array as checked -- this array will be passed for create / update
                    currentIngredient.setChecked(true);


                } else {
                    holder.getIngredientTextView().setPaintFlags(0);
                    holder.getIngredientTextView().setTextColor(ContextCompat.getColor(ctx, R.color.black));

                    holder.getAmountTextView().setPaintFlags(0);
                    holder.getAmountTextView().setTextColor(ContextCompat.getColor(ctx, R.color.black));

                    holder.getCostTextView().setPaintFlags(0);
                    holder.getCostTextView().setTextColor(ContextCompat.getColor(ctx, R.color.gold));

                    currentIngredient.setChecked(false);
                }
            }
        });


        // listener for the add and minus button next to amount
        holder.getAddButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentAmount = shoppingListIngredientList.get(position).getAmount();

                // only execute -- condition to be added
                if (true){
                    shoppingListIngredientList.get(position).incrementAmount(1);
                    holder.getAmountTextView().setText(String.valueOf(currentAmount + 1));
                }
            }
        });

        holder.getMinusButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentAmount = shoppingListIngredientList.get(position).getAmount();

                // only execute if > 1 (min = 1)
                if (currentAmount > 1){
                    shoppingListIngredientList.get(position).decrementAmount(1);
                    holder.getAmountTextView().setText(String.valueOf(currentAmount - 1));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingListIngredientList.size();
    }
}
