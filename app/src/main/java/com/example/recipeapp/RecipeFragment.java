package com.example.recipeapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ArrayList<Recipe> recipes = new ArrayList<>();
    // Load sample recipes data
    Recipe r1 = new Recipe("Apple", null, "", 0,
            null, null, "", 0);
    Recipe r2 = new Recipe("Orange", null, "", 0,
            null, null, "", 0);
    Recipe r3 = new Recipe("Banana", null, "", 0,
            null, null, "", 0);

    ListView listView;   // listview to display recipes
    AutoCompleteTextView autoCompleteTextView;


    public RecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeFragment newInstance(String param1, String param2) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Load sample data
        recipes.add(r1);
        recipes.add(r2);
        recipes.add(r3);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        // Inflate the list of items
        listView = (ListView) view.findViewById(R.id.recipes_listView);
        RecipeAdapter recipeAdapter = new RecipeAdapter(getContext().getApplicationContext(), recipes);
        listView.setAdapter(recipeAdapter);

        // set up listener for search view
        SearchView searchView = (SearchView) view.findViewById(R.id.recipes_searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // when text is changed, filter search results
                recipeAdapter.getFilter().filter(newText);
                return false;
            }
        });

        // Load the dropdown menu for ordering
        autoCompleteTextView = view.findViewById(R.id.recipe_dropDown);
        String[] orderingStringArray = getResources().getStringArray(R.array.orderBy);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext().getApplicationContext(), R.layout.recipe_dropdown_item, orderingStringArray);   // populate with recipe dropdown item which is a single textview.
        autoCompleteTextView.setAdapter(arrayAdapter);
        // Listener to make the view show dropdown
        autoCompleteTextView.setOnClickListener(view1 -> autoCompleteTextView.showDropDown());

        // Listener to do something to the selected object
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedOption = getResources().getStringArray(R.array.orderBy)[i];    // i is the position of selected elem
                Log.d("Dropdown menu", selectedOption);
            }
        });

        // the final return object
        return view;
    }

}