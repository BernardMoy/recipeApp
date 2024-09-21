package com.example.recipeapp;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingListIngredientRecyclerViewHolder extends RecyclerView.ViewHolder {

    private TextView ingredientTextView;
    private TextView amountTextView;
    private TextView costTextView;
    private ImageButton deleteButton;
    private TableRow tableRow;
    private ToggleButton checkButton;
    private ImageButton addButton;
    private ImageButton minusButton;

    public ShoppingListIngredientRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        ingredientTextView = itemView.findViewById(R.id.ingredient_textView);
        amountTextView = itemView.findViewById(R.id.amount_textView);
        costTextView = itemView.findViewById(R.id.cost_textView);
        deleteButton = itemView.findViewById(R.id.ingredientDelete_button);
        tableRow = itemView.findViewById(R.id.shoppingListIngredient_tableRow);
        checkButton = itemView.findViewById(R.id.ingredient_checkButton);
        addButton = itemView.findViewById(R.id.ingredientAdd_ImageButton);
        minusButton = itemView.findViewById(R.id.ingredientMinus_ImageButton);
    }

    public TextView getIngredientTextView() {
        return ingredientTextView;
    }

    public TextView getAmountTextView() {
        return amountTextView;
    }

    public TextView getCostTextView() {
        return costTextView;
    }

    public ImageButton getDeleteButton() {
        return deleteButton;
    }

    public TableRow getTableRow() {
        return tableRow;
    }

    public ToggleButton getCheckButton() {
        return checkButton;
    }

    public ImageButton getMinusButton() {
        return minusButton;
    }

    public ImageButton getAddButton() {
        return addButton;
    }
}
