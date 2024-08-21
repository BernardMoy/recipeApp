package com.example.recipeapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private ArrayList<RecipePreview> recipePreviewArrayList;
    RecyclerView recipeRecyclerView;

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



        /*
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
        */


        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

        // load recipe recycler view
        recipeRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_recyclerView);

        // load recipe preview arraylist from db
        recipePreviewArrayList = displayRecipesFromDatabase();
        // set up recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recipeRecyclerView.setLayoutManager(linearLayoutManager);
        RecipeAdapter recipeAdapter = new RecipeAdapter(getActivity().getApplicationContext(), recipePreviewArrayList);
        recipeRecyclerView.setAdapter(recipeAdapter);

        return view;
    }

    public ArrayList<RecipePreview> displayRecipesFromDatabase(){
        ArrayList<RecipePreview> recipePreviewArrayList = new ArrayList<>();

        // extract all recipes data from the database
        DatabaseHelper db = new DatabaseHelper(getActivity().getApplicationContext());
        Cursor cursor = db.getRecipes();

        // if have data
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                int recipeId = cursor.getInt(0);
                String name = cursor.getString(1);
                float prepTime = cursor.getFloat(2);
                int timesCooked = cursor.getInt(3);
                boolean isFavourited = (cursor.getInt(4) == 1);
                byte[] image = cursor.getBlob(5);

                // Extract the first tag and also the count
                String tag = "";
                int tagPlus = 0;
                Cursor cursor2 = db.getTagsPreview(recipeId);
                if (cursor2.getCount() > 0){
                    cursor2.moveToNext();
                    tag = cursor2.getString(0);
                    tagPlus = cursor2.getInt(1) - 1;
                }

                // Extract the total weighted cost
                float cost = 0.0f;
                Cursor cursor3 = db.getWeightedCost(recipeId);
                if (cursor3.getCount() > 0){
                    cursor3.moveToNext();
                    cost = cursor3.getFloat(0);
                }

                // Construct recipe preview object
                RecipePreview recipePreview = new RecipePreview(recipeId, name, tag, tagPlus, cost, prepTime, isFavourited, timesCooked, image);
                recipePreviewArrayList.add(recipePreview);
            }
        }
        return recipePreviewArrayList;
    }

}