package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TagFilterAdapter extends RecyclerView.Adapter<TagFilterRecyclerViewHolder> {

    // This is known as recipeTagsFilter_ (Button / recyclerview etc).

    // Used to display a single list of strings in a format.
    // Which is deleted when clicked on.

    private Context ctx;
    private List<String> stringList;

    public TagFilterAdapter(Context ctx, List<String> stringList){
        this.ctx = ctx;
        this.stringList = stringList;
    }

    @NonNull
    @Override
    public TagFilterRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TagFilterRecyclerViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_box_gray, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TagFilterRecyclerViewHolder holder, int position) {
        holder.getTextView().setText(stringList.get(position));
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }
}
