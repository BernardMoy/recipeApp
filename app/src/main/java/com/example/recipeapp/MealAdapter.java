package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MealAdapter extends RecyclerView.Adapter<MealRecyclerViewHolder> {

    // an arraylist to store a list of meal preview objects.
    private Context ctx;
    private ArrayList<MealPreview> mealPreviewList;

    public MealAdapter(Context ctx, ArrayList<MealPreview> mealPreviewList){
        this.ctx = ctx;
        this.mealPreviewList = mealPreviewList;
    }

    @NonNull
    @Override
    public MealRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MealRecyclerViewHolder(LayoutInflater.from(ctx).inflate(R.layout.meal_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MealRecyclerViewHolder holder, int position) {
        // set text for the meal rows
    }

    @Override
    public int getItemCount() {
        return mealPreviewList.size();
    }
}
