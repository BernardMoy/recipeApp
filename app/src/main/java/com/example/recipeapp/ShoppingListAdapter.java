package com.example.recipeapp;

import static androidx.core.content.ContextCompat.getDrawable;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListRecyclerViewHolder> implements Filterable {

    private Context ctx;
    private ArrayList<ShoppingListPreview> shoppingListPreviewList;
    private ArrayList<ShoppingListPreview> shoppingListPreviewListFull;
    private boolean favouriteFilterSelected;

    // two buttons for the dialog that pops up when delete button is clicked
    private Button cancelButton;
    private Button deleteButton;


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

        // discard the prev listener
        holder.getFavouriteButton().setOnCheckedChangeListener(null);

        // change the displayed icon depending whether is favourited
        ShoppingListPreview currentShoppingList = shoppingListPreviewList.get(position);

        holder.getFavouriteButton().setChecked(currentShoppingList.isFavourited());

        // set the fav button
        holder.getFavouriteButton().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                // Extract the corresponding id that is clicked
                int pos = holder.getAdapterPosition();

                // must use recipePreviewList instead of full. Operation is done on filtered results.
                int clickedShoppingListId = shoppingListPreviewList.get(pos).getShoppingListId();
                DatabaseHelperShoppingLists db = new DatabaseHelperShoppingLists(ctx);

                if (b) {
                    db.updateShoppingListFavourite(clickedShoppingListId);
                    shoppingListPreviewList.get(pos).setIsFavourited(true);

                } else {
                    db.updateShoppingListUnFavourite(clickedShoppingListId);
                    shoppingListPreviewList.get(pos).setIsFavourited(false);
                }
                db.close();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Extract the corresponding recipe id that is clicked
                int pos = holder.getAdapterPosition();
                int clickedShoppingListId = shoppingListPreviewList.get(pos).getShoppingListId();

                // Start intent with passed parameters of shopping list
                Intent i = new Intent(ctx, AddNewShoppingList.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                // pass the shopping list id to the edit shopping list activity.
                // Information is extracted from db there
                i.putExtra("shopping_list_id", clickedShoppingListId);
                i.putExtra("title_text", "Edit shopping list");

                ctx.startActivity(i);
            }
        });

        // functionality of delete button
        holder.getDeleteShoppingListButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set up dialog
                Dialog dialog = new Dialog(ctx);
                dialog.setContentView(R.layout.confirm_window);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(getDrawable(ctx, R.drawable.custom_edit_text));
                dialog.setCancelable(false);

                // load 2 buttons
                cancelButton = dialog.findViewById(R.id.confirmRecipeCancel_button);
                deleteButton = dialog.findViewById(R.id.confirmRecipeDelete_button);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = holder.getAdapterPosition();
                        int clickedShoppingListId = shoppingListPreviewList.get(pos).getShoppingListId();

                        // delete SL from arraylists
                        shoppingListPreviewList.remove(pos);

                        for (int i = 0 ; i < shoppingListPreviewListFull.size() ; i++){
                            if (shoppingListPreviewListFull.get(i).getShoppingListId() == clickedShoppingListId){
                                shoppingListPreviewListFull.remove(i);
                                break;
                            }
                        }

                        notifyItemRemoved(pos);

                        // update displayed count
                        String countString = String.valueOf(shoppingListPreviewList.size()) + " results";
                        TextView textView = (TextView) ((Activity) ctx).findViewById(R.id.shoppingListCount_textView);
                        textView.setText(countString);

                        // display empty message if all recipes are deleted
                        if (shoppingListPreviewList.isEmpty()){
                            TextView emptyRecipeTextView = (TextView) ((Activity) ctx).findViewById(R.id.emptyShoppingList_textView);
                            emptyRecipeTextView.setVisibility(View.VISIBLE);
                        }

                        // delete SL from db
                        DatabaseHelperShoppingLists db = new DatabaseHelperShoppingLists(ctx);
                        db.deleteShoppingListFromId(clickedShoppingListId);
                        db.close();
                        // dismiss dialog
                        dialog.dismiss();

                    }
                });

                dialog.show();
            }
        });

        // display the percentage of SL that is completed
        DatabaseHelperShoppingLists db = new DatabaseHelperShoppingLists(ctx);

        // first get count of the ingredients
        Cursor cursor = db.getShoppingListsNumsFromId(currentShoppingList.getShoppingListId());
        cursor.moveToNext();

        // if empty, display empty message
        if (cursor.getInt(0) == 0){
            String text = "  No ingredients";
            holder.getStatusTextView().setText(text);
            holder.getStatusTextView().setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.circle_icon_gray, 0, 0, 0);

        } else {
            // get the percentage
            Cursor cursor1 = db.getShoppingListPercentage(currentShoppingList.getShoppingListId());
            cursor1.moveToNext();
            int percentage = Math.round(cursor1.getFloat(0)*100);
            String text = "  " + String.valueOf(percentage) + "% Completed";
            holder.getStatusTextView().setText(text);

            if (percentage == 0){
                holder.getStatusTextView().setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.circle_icon_red, 0, 0, 0);

            } else if (percentage == 100){
                holder.getStatusTextView().setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.circle_icon_green, 0, 0, 0);

            } else {
                holder.getStatusTextView().setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.circle_icon_orange, 0, 0, 0);
            }
        }

        db.close();
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
                // filter by the fav filter only
                for (ShoppingListPreview shoppingListPreview : shoppingListPreviewListFull){
                    if (validateShoppingList(shoppingListPreview)){
                        filteredShoppingListPreviewList.add(shoppingListPreview);
                    }
                }

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
        if (favouriteFilterSelected && !shoppingListPreview.isFavourited()){
            return false;
        }

        return true;
    }

    public void setFavouriteFilterSelected(boolean favouriteFilterSelected) {
        this.favouriteFilterSelected = favouriteFilterSelected;
    }
}
