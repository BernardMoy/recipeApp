package com.example.recipeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        MealPreview mealPreview = mealPreviewList.get(position);

        // set text for the meal rows
        holder.getCategoryName().setText(mealPreview.getCategory());

        byte[] imageByteArray = mealPreview.getRecipeImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        holder.getRecipeImage().setImageBitmap(bitmap);

        holder.getRecipeName().setText(mealPreview.getRecipeName());

        holder.getRecipeCost().setText(String.valueOf(mealPreview.getRecipeCost()));
    }

    @Override
    public int getItemCount() {
        return mealPreviewList.size();
    }
}
