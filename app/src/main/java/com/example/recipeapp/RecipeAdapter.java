package com.example.recipeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import java.util.ArrayList;

public class RecipeAdapter extends BaseAdapter implements Filterable {

    Context ctx;
    ArrayList<Recipe> recipes;
    LayoutInflater inflater;
    RecipeFilter recipeFilter;

    public RecipeAdapter(Context ctx, ArrayList<Recipe> recipes){
        this.ctx = ctx;
        this.recipes = recipes;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.recipe_list_item, null);
        TextView tv = (TextView) convertView.findViewById(R.id.row_text);
        ImageView iv = (ImageView) convertView.findViewById(R.id.row_image);
        tv.setText(this.recipes.get(position).getRecipeName());
        iv.setImageResource(this.recipes.get(position).getRecipeImage());

        return convertView;
    }

    @Override
    public Filter getFilter(){
        if (recipeFilter == null){
            // create new filter
            recipeFilter = new RecipeFilter();

        }
        return recipeFilter;
    }

    class RecipeFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length()>0){
                // search query with filter text
                constraint = constraint.toString().toLowerCase();

                ArrayList<Recipe> filtered_recipes = new ArrayList<>();

                // filtering logic goes here
                for (int i = 0 ; i < recipes.size(); i++){
                    if (recipes.get(i).getRecipeName().toLowerCase().contains(constraint)){
                        filtered_recipes.add(recipes.get(i));

                    }

                    // adjust result
                    results.count = filtered_recipes.size();
                    results.values = filtered_recipes;
                }
            }
            else{
                // search query without any filter text
                results.count = recipes.size();
                results.values = recipes;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            recipes = (ArrayList<Recipe>) results.values;   // Update the displayed list of results
            notifyDataSetChanged();
        }
    }
}
