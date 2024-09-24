package com.example.recipeapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientRecyclerViewHolder> {

    // display a row of ingredient, portionSize, supermarket and cost
    private Context ctx;
    private List<Ingredient> ingredientList;

    public IngredientAdapter(Context ctx, List<Ingredient> ingredientList){
        this.ctx = ctx;
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public IngredientRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientRecyclerViewHolder(LayoutInflater.from(ctx).inflate(R.layout.ingredient_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientRecyclerViewHolder holder, int position) {

        Ingredient currentIngredient = ingredientList.get(position);

        // set text for the item row
        holder.getIngredientTextView().setText(currentIngredient.getIngredient());
        holder.getAmountTextView().setText(String.valueOf(currentIngredient.getAmount()));
        holder.getSupermarketTextView().setText(currentIngredient.getSupermarket());
        holder.getCostTextView().setText(String.valueOf(currentIngredient.getCost()));
        holder.getShelfLifeTextView().setText(String.valueOf(currentIngredient.getShelfLife()));

        // set alternating row colours
        if (position % 2 == 0){
            holder.getTableRow().setBackgroundResource(R.color.lightColor);
        }

        // Make the displayed box disappear when clicked the delete button
        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                if (position != -1){   // prevent clicking too fast
                    ingredientList.remove(position);
                    notifyDataSetChanged();  // make the colors of the position appears as correct

                    // Update cost through a reference to activity using passed in context
                    AddNewRecipe a = (AddNewRecipe) ctx;
                    a.updateCost();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

}
