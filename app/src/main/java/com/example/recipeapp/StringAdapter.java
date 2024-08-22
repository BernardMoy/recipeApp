package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StringAdapter extends RecyclerView.Adapter<StringRecyclerViewHolder> {

    // Used to display a single list of strings in a format.
    // Which is deleted when clicked on.

    private Context ctx;
    private List<String> stringList;

    public StringAdapter(Context ctx, List<String> stringList){
        this.ctx = ctx;
        this.stringList = stringList;
    }

    @NonNull
    @Override
    public StringRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StringRecyclerViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_box, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StringRecyclerViewHolder holder, int position) {
        holder.getTextView().setText(stringList.get(position));

        // Make the displayed box disappear when clicked on
        holder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                if (position != -1){  // prevent clicking too fast
                    stringList.remove(position);
                    notifyItemRemoved(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }
}
