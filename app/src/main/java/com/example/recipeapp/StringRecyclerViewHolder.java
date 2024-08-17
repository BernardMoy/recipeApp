package com.example.recipeapp;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class StringRecyclerViewHolder extends ViewHolder {

    private TextView textView;

    public StringRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.boxText);


    }

    public TextView getTextView(){
        return textView;
    }
}
