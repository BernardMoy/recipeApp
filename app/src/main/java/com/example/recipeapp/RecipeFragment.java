package com.example.recipeapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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



    // For displaying recipe previews
    private ArrayList<RecipePreview> recipePreviewArrayList;
    private RecyclerView recipeRecyclerView;
    private RecipeAdapter recipeAdapter;  // The adapter for displaying a list of recipe previews

    // For displaying ordering options
    private AutoCompleteTextView autoCompleteTextView;

    // For the opening recipe tags filter button UI
    private ImageButton recipeTagsFilterButton;
    private boolean tagFilterMenuOpened;
    private TextView recipeFilterHintTextView;

    // For displaying a list of all tags
    private ArrayList<String> tagList;  // an arraylist that stores all tags across all recipes
    private RecyclerView recipeTagsFilterRecyclerView;
    private TagFilterAdapter recipeTagsFilterAdapter;

    // For managing selected tags from the list above
    private Set<String> selectedTagsSet;   // a hashset to store all selected tags from filters (Order does not matter)




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

        View view = inflater.inflate(R.layout.fragment_recipe, container, false);

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

        // load recipe recycler view
        recipeRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_recyclerView);


        // Sets on click listener for the recipe tags filter button.
        // make the hint button and recyclerview not visible
        recipeFilterHintTextView = view.findViewById(R.id.searchFilter_hint);
        recipeFilterHintTextView.setVisibility(View.GONE);

        // set on click listener for the button of search bar filter
        recipeTagsFilterButton = view.findViewById(R.id.recipeTagsFilter_button);
        recipeTagsFilterRecyclerView = view.findViewById(R.id.recipeTagsFilter_recyclerView);
        recipeTagsFilterRecyclerView.setVisibility(View.GONE);

        // state is initially false
        tagFilterMenuOpened = false;
        // recycler view is initially not visible
        recipeTagsFilterRecyclerView.setVisibility(View.GONE);

        recipeTagsFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get current context
                Context ctx = getActivity().getApplicationContext();

                // conditional button for opening and closing
                if (!tagFilterMenuOpened){
                    // visuals
                    recipeTagsFilterButton.setImageResource(R.drawable.arrow_drop_up_icon);
                    recipeTagsFilterButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.secondaryColor));
                    recipeFilterHintTextView.setVisibility(View.VISIBLE);

                    recipeTagsFilterRecyclerView.setVisibility(View.VISIBLE);

                    // toggle the state
                    tagFilterMenuOpened = true;

                } else {
                    // visuals
                    recipeTagsFilterButton.setImageResource(R.drawable.filter_list_icon);
                    recipeTagsFilterButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.veryLightColor));
                    recipeFilterHintTextView.setVisibility(View.GONE);
                    recipeTagsFilterRecyclerView.setVisibility(View.GONE);

                    // toggle the state
                    tagFilterMenuOpened = false;
                }
            }
        });


        // Set up query text listener for search view.
        SearchView searchView = (SearchView) view.findViewById(R.id.recipes_searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // when text is changed, filter search results
                Filter recipeFilter = recipeAdapter.getFilter();

                recipeFilter.filter(s, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int i) {
                        // Updates the recipe count after filtering
                        String countString = String.valueOf(i) + " results";
                        TextView textView = (TextView) view.findViewById(R.id.recipeCount_textView);
                        textView.setText(countString);
                    }
                });
                return false;
            }
        });

        // Set up on click listener for each box of the tag filter recycler view
        selectedTagsSet = new HashSet<>();
        recipeTagsFilterRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(childView);
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the string of the tag at that position
                        String clickedTag = tagList.get(position);
                        Log.d("TAG CLICKED", clickedTag);

                        // Add the selected item to the set
                        selectedTagsSet.add(clickedTag);

                        // get the view associated with that child view position
                        TextView item = (TextView) rv.getChildAt(position);

                        // change the appearance of boxes depending whether or not they are in set
                        item.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primaryColor));
                        item.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        return view;
    }

    // Called everytime when the recipe menu reloads
    @Override
    public void onResume(){
        // Updates the recipe displayed whenever the page is loaded
        super.onResume();

        View view = getView();

        // 1. Updates the recipe count displayed
        int count = displayRecipesCountFromDatabase();
        String countString = String.valueOf(count) + " results";
        TextView textView = (TextView) view.findViewById(R.id.recipeCount_textView);
        textView.setText(countString);

        // If there are no recipes, display empty recipe message
        TextView emptyRecipeTextView = view.findViewById(R.id.emptyRecipes_textView);
        if (count > 0){
            emptyRecipeTextView.setVisibility(View.GONE);
        } else {
            emptyRecipeTextView.setVisibility(View.VISIBLE);
        }


        // 2. Update the recipe recyclerview: load recipe preview arraylist from db
        recipePreviewArrayList = displayRecipesFromDatabase();
        // set up recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recipeRecyclerView.setLayoutManager(linearLayoutManager);

        // crete a new adapter from the modified recipe preview array list
        recipeAdapter = new RecipeAdapter(getActivity().getApplicationContext(), recipePreviewArrayList);
        recipeRecyclerView.setAdapter(recipeAdapter);


        // 3. Update the recipe tags filter recyclerview: load the recipe tag filter recycler view
        Context ctx = getContext();
        DatabaseHelper db = new DatabaseHelper(ctx);

        // Extract all tags and fill the tagList
        tagList = new ArrayList<>();

        Cursor cursor = db.getTags();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                tagList.add(cursor.getString(0));
            }
        }
        // all tags will be deselected (through reloading the tag filter adapter)
        // reset the selected tags hashset
        selectedTagsSet = new HashSet<>();

        // populate the tags filter recyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ctx, 4, GridLayoutManager.VERTICAL, false);
        recipeTagsFilterRecyclerView.setLayoutManager(gridLayoutManager);
        recipeTagsFilterAdapter = new TagFilterAdapter(ctx, tagList); // to be referenced later
        recipeTagsFilterRecyclerView.setAdapter(recipeTagsFilterAdapter);
    }

    public int displayRecipesCountFromDatabase(){
        DatabaseHelper db = new DatabaseHelper(getActivity().getApplicationContext());
        Cursor cursor = db.getRecipesCount();

        // there is guaranteed to have one data
        if (cursor.getCount() > 0){
            cursor.moveToNext();
            return cursor.getInt(0);
        } else {
            return 0;
        }
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
                Cursor cursor2 = db.getTagsCount(recipeId);
                if (cursor2.getCount() > 0){
                    cursor2.moveToNext();
                    // Get the tag count
                    int tagCount = cursor2.getInt(0);
                    if (tagCount > 1){
                        tagPlus = tagCount - 1;
                    }
                    // If there are at least one tag, extract it
                    if (tagCount > 0){
                        Cursor cursor4 = db.getTagPreview(recipeId);
                        if (cursor4.getCount() > 0){
                            cursor4.moveToNext();
                            tag = cursor4.getString(0);
                        }
                    }
                }
                // no tag: Tag = "", tagPlus = 0
                // one tag: Tag = tag, tagPlus = 0
                // >one tag: Tag = tag, tagPlus > 0

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