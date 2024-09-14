package com.example.recipeapp;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class StartsWithFilterArrayAdapter extends ArrayAdapter {

    private ArrayList<String> list;
    private ArrayList<String> listFull;

    public StartsWithFilterArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> list) {
        super(context, resource, list);
        this.listFull = list;
        this.list = new ArrayList<>(listFull);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter(){
        return customFilter;
    }

    private final Filter customFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<String> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0){
                // filter all
                filteredList.addAll(listFull);

            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (String s : listFull){
                    if (s.toLowerCase().startsWith(filterPattern)){
                        filteredList.add(s);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            filterResults.count = filteredList.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

}
