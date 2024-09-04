package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListRecyclerViewHolder> implements Filterable {

    private Context ctx;
    private ArrayList<ShoppingListPreview> shoppingListPreviewList;
    private ArrayList<ShoppingListPreview> shoppingListPreviewListFull;
    private boolean favouriteFilterSelected;


    public ShoppingListAdapter(Context ctx, ArrayList<ShoppingListPreview> shoppingListPreviewList){
        this.ctx = ctx;
        this.shoppingListPreviewListFull = shoppingListPreviewList;
        this.shoppingListPreviewList = new ArrayList<>(shoppingListPreviewListFull);
    }

    @NonNull
    @Override
    public ShoppingListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShoppingListRecyclerViewHolder(LayoutInflater.from(ctx).inflate(R.layout.shopping_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListRecyclerViewHolder holder, int position) {

        ShoppingListPreview shoppingListPreview = shoppingListPreviewList.get(position);

        holder.getNameTextView().setText(shoppingListPreview.getName());

        String itemCountString = String.valueOf(shoppingListPreview.getItemCount()) + " items";
        holder.getItemCountTextView().setText(String.valueOf(itemCountString));

        String supermarketCountString = "(" + String.valueOf(shoppingListPreview.getSupermarketCount()) + " places)";
        holder.getSupermarketCountTextView().setText(supermarketCountString);

        holder.getCostTextView().setText(String.valueOf(shoppingListPreview.getCost()));
    }

    @Override
    public int getItemCount() {
        return shoppingListPreviewList.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingListPreviewsFilter;
    }

    private final Filter shoppingListPreviewsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ShoppingListPreview> filteredShoppingListPreviewList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredShoppingListPreviewList.addAll(shoppingListPreviewListFull);

            } else {
                // filter by search string
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ShoppingListPreview shoppingListPreview : shoppingListPreviewListFull){
                    if (shoppingListPreview.getName().toLowerCase().contains(filterPattern) && validateShoppingList(shoppingListPreview)){
                        filteredShoppingListPreviewList.add(shoppingListPreview);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredShoppingListPreviewList;
            results.count = filteredShoppingListPreviewList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            // shoppingListPreviewList is the list to be displayed
            shoppingListPreviewList.clear();
            shoppingListPreviewList.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };


    // validate SL with extra constraints for the filter
    public boolean validateShoppingList(ShoppingListPreview shoppingListPreview){
        return true;
    }
}
