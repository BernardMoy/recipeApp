package com.example.recipeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeRecyclerViewHolder> {

    // display a list of recipes in the recipe fragment page
    private Context ctx;
    private ArrayList<RecipePreview> recipePreviewList;

    public RecipeAdapter(Context ctx, ArrayList<RecipePreview> recipePreviewList){
        this.ctx = ctx;
        this.recipePreviewList = recipePreviewList;
    }

    @NonNull
    @Override
    public RecipeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeRecyclerViewHolder(LayoutInflater.from(ctx).inflate(R.layout.recipe_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeRecyclerViewHolder holder, int position) {
        holder.getName().setText(recipePreviewList.get(position).getName());
        holder.getTag().setText(recipePreviewList.get(position).getTag());
        String tagPlusString = " +" + String.valueOf(recipePreviewList.get(position).getTagPlus());
        holder.getTagPlus().setText(tagPlusString);
        holder.getCost().setText(String.valueOf(recipePreviewList.get(position).getCost()));
        holder.getPrepTime().setText(String.valueOf(recipePreviewList.get(position).getPrepTime()));
        holder.getTimesCooked().setText(String.valueOf(recipePreviewList.get(position).getTimesCooked()));
        byte[] imageByteArray = recipePreviewList.get(position).getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        holder.getImage().setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return recipePreviewList.size();
    }
}
