package com.example.recipeapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeRecyclerViewHolder extends RecyclerView.ViewHolder {

    // these are the variables that are DISPLAYED, not all variables of a recipe
    private TextView name;
    private TextView tag;
    private TextView tagPlus;
    private TextView cost;
    private TextView prepTime;
    private TextView timesCooked;
    private ImageView image;

    public RecipeRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.recipeRow_name);
        tag = itemView.findViewById(R.id.recipeRow_tag);
        tagPlus = itemView.findViewById(R.id.recipeRow_tagPlus);
        cost = itemView.findViewById(R.id.recipeRow_cost);
        prepTime = itemView.findViewById(R.id.recipeRow_prepTime);
        timesCooked = itemView.findViewById(R.id.recipeRow_timesCooked);
        image = itemView.findViewById(R.id.recipeRow_image);
    }

    public TextView getName() {
        return name;
    }

    public TextView getTag() {
        return tag;
    }

    public TextView getTagPlus() {
        return tagPlus;
    }

    public TextView getCost() {
        return cost;
    }

    public TextView getPrepTime() {
        return prepTime;
    }

    public TextView getTimesCooked() {
        return timesCooked;
    }

    public ImageView getImage() {
        return image;
    }
}
