package com.example.recipeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

        String firstTag = recipePreviewList.get(position).getTag();
        if (!firstTag.isEmpty()){
            // first tag is not empty: There are tags (>=1)
            holder.getTag().setText(recipePreviewList.get(position).getTag());

            int tagPlus = recipePreviewList.get(position).getTagPlus();
            if (tagPlus != 0){
                // There is more than one tag (>1)
                String tagPlusString = " +" + String.valueOf(tagPlus);
                holder.getTagPlus().setText(tagPlusString);
            } else {
                // There is only one tag: Set tagplus to be nothing (=1)
                holder.getTagPlus().setText("");
            }
        } else {
            // There are no tags at all (=0)
            // format the first tag textview into regular text saying no tags
            holder.getTag().setText("No tags");
            holder.getTag().setTextColor(ContextCompat.getColor(ctx, R.color.gray));
            holder.getTag().setBackgroundColor(Color.TRANSPARENT);
            holder.getTag().setPadding(0,0,0,0);
            // remove the tag plus text
            holder.getTagPlus().setText("");
        }


        holder.getCost().setText(String.valueOf(recipePreviewList.get(position).getCost()));

        String prepTimeString = " " + String.valueOf(recipePreviewList.get(position).getPrepTime()) + " minutes";
        holder.getPrepTime().setText(prepTimeString);

        String timesCookedString = " " + String.valueOf(String.valueOf(recipePreviewList.get(position).getTimesCooked())) + " times cooked";
        holder.getTimesCooked().setText(timesCookedString);

        byte[] imageByteArray = recipePreviewList.get(position).getImage();

        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        holder.getImage().setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return recipePreviewList.size();
    }
}
