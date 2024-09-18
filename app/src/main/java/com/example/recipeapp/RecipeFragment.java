package com.example.recipeapp;

import static androidx.core.content.ContextCompat.getColor;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashSet;

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
    private ToggleButton recipeTagsFilterButton;
    private boolean tagFilterMenuOpened;
    private TextView recipeFilterHintTextView;

    // For displaying a list of all tags
    private ArrayList<String> tagList;  // an arraylist that stores all tags across all recipes
    private RecyclerView recipeTagsFilterRecyclerView;
    private TagFilterAdapter recipeTagsFilterAdapter;

    // For managing selected tags from the list above
    private HashSet<String> selectedTagsSet;   // a hashset to store all selected tags from filters (Order does not matter)

    // the string for filtering recipe names
    private String searchString;

    // toggle button for the fav symbol
    private ToggleButton favToggleButton;

    // for the top bar that is displayed when some items are selected
    private ConstraintLayout selectedOptionsConstraintLayout;
    private LinearLayout filterOptionsLinearLayout;


    // 0 = latest, 1 = lowest cost, 2 = most frequent;
    private int orderingOption;


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

    /**
     * loads all the class variables when the app is initiated.
     * buttons onclick listeners, search on query change listeners, tags filter on click listeners
     * and fav toggle button on checked listener are set up here.
     *
     * Items are not being loaded here.
     *
     * Whenever filtering is needed, use filterRecipes(View view) function.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view created
     *
     */
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

        // ordering option is initially 0
        orderingOption = 0;

        // Listener to do something to the selected object
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                // i is the position clicked
                // 0 = latest added, 1 = lowest cost, 2 = most frequently cooked
                orderingOption = i;

                // call the onResume method -> Reload the entire recyclerview using same logic
                onResume();
            }
        });

        // load recipe recycler view
        recipeRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_recyclerView);

        // load top bar
        selectedOptionsConstraintLayout = view.findViewById(R.id.selectedOptions_constraintLayout);
        filterOptionsLinearLayout = view.findViewById(R.id.filterOptions_linearLayout);

        // Sets on click listener for the recipe tags filter button.
        // make the hint button and recyclerview not visible
        recipeFilterHintTextView = view.findViewById(R.id.searchFilter_hint);
        recipeFilterHintTextView.setVisibility(View.GONE);

        // set on click listener for the button of search bar filter
        recipeTagsFilterButton = view.findViewById(R.id.recipeTagsFilter_button);
        recipeTagsFilterRecyclerView = view.findViewById(R.id.recipeTagsFilter_recyclerView);
        recipeTagsFilterRecyclerView.setVisibility(View.GONE);


        // recycler view is initially not visible
        recipeTagsFilterRecyclerView.setVisibility(View.GONE);

        recipeTagsFilterButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    recipeFilterHintTextView.setVisibility(View.VISIBLE);
                    recipeTagsFilterRecyclerView.setVisibility(View.VISIBLE);

                } else {
                    recipeFilterHintTextView.setVisibility(View.GONE);
                    recipeTagsFilterRecyclerView.setVisibility(View.GONE);

                }
            }
        });


        searchString = "";
        // Set up query text listener for search view.
        SearchView searchView = (SearchView) view.findViewById(R.id.recipes_searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // update class variable
                searchString = s;
                // when text is changed, filter search results
                filterRecipes(view);
                return false;
            }
        });

        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        // Set search text color
        textView.setTextColor(getColor(getContext(), R.color.gray));
        // Set search hints color
        textView.setHintTextColor(getColor(getContext(), R.color.gray));

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

                        TextView item = (TextView) rv.getChildAt(position);

                        // case when selected tag in selected list (Already selected)
                        if (selectedTagsSet.contains(clickedTag)){
                            // deselect it
                            selectedTagsSet.remove(clickedTag);
                            // update the adapter
                            recipeAdapter.setSelectedTagList(selectedTagsSet);

                            // change the appearance of boxes depending whether or not they are in set
                            item.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.light_color_background));
                            item.setTextColor(getColor(getContext(), R.color.primaryColor));

                        } else {
                            // Add the selected item to the set
                            selectedTagsSet.add(clickedTag);
                            // update the adapter
                            recipeAdapter.setSelectedTagList(selectedTagsSet);

                            // change the appearance of boxes depending whether or not they are in set
                            item.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.primary_color_background));
                            item.setTextColor(getColor(getContext(), R.color.white));
                        }

                        // when a position is clicked or the view is reset (onResume()), filter recipes.
                        filterRecipes(view);
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


        // set up state change listener for the favourite toggle button
        favToggleButton = view.findViewById(R.id.fav_toggleButton);
        favToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // update the filtering options in the filter in adapter
                recipeAdapter.setFavouriteFilterSelected(b);
                filterRecipes(view);
            }
        });

        return view;
    }

    /**
     * Called everytime when the recipe screen is reloaded, including
     * exiting activity after a recipe is created / edited,
     * moved from another fragment,
     * and the initial loading.
     *
     * Whenever filtering is needed, call the filterRecipe(View view) function.
     */
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        recipeRecyclerView.setLayoutManager(linearLayoutManager);

        // crete a new adapter from the modified recipe preview array list
        recipeAdapter = new RecipeAdapter(getActivity(), recipePreviewArrayList, selectedOptionsConstraintLayout, filterOptionsLinearLayout);
        recipeRecyclerView.setAdapter(recipeAdapter);

        // if there are no items, make the selected blue top bar not visible
        if (recipePreviewArrayList.isEmpty()) {
            selectedOptionsConstraintLayout.setVisibility(View.GONE);
            selectedOptionsConstraintLayout.setEnabled(false);
            filterOptionsLinearLayout.setVisibility(View.VISIBLE);
            filterOptionsLinearLayout.setEnabled(true);
        }

        // 3. Update the recipe tags filter recyclerview: load the recipe tag filter recycler view
        Context ctx = getContext();
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);

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
        // update the adapter
        recipeAdapter.setSelectedTagList(selectedTagsSet);

        // the fav button will be defaulted to false -> set it to be unchecked
        favToggleButton.setChecked(false);

        // filter results
        filterRecipes(view);

        // populate the tags filter recyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ctx, 4, GridLayoutManager.VERTICAL, false);
        recipeTagsFilterRecyclerView.setLayoutManager(gridLayoutManager);
        recipeTagsFilterAdapter = new TagFilterAdapter(ctx, tagList); // to be referenced later
        recipeTagsFilterRecyclerView.setAdapter(recipeTagsFilterAdapter);
        db.close();
    }

    public int displayRecipesCountFromDatabase(){
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(getActivity().getApplicationContext());
        Cursor cursor = db.getRecipesCount();
        int count = 0;

        // there is guaranteed to have one data
        cursor.moveToNext();
        count = cursor.getInt(0);

        db.close();
        return count;
    }

    public ArrayList<RecipePreview> displayRecipesFromDatabase(){
        ArrayList<RecipePreview> recipePreviewArrayList = new ArrayList<>();

        // extract all recipes data from the database
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(getActivity().getApplicationContext());
        Cursor cursor = db.getRecipes(orderingOption);

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
        db.close();
        return recipePreviewArrayList;
    }

    // method to update the recipeAdapter with a filter.
    // Called when the search view is updated or a new tag is selected or deselected
    public void filterRecipes(View view){
        // when text is changed, filter search results
        Filter recipeFilter = recipeAdapter.getFilter();

        // filter based on the class variable searchString
        recipeFilter.filter(searchString, new Filter.FilterListener() {
            @Override
            public void onFilterComplete(int i) {
                // Updates the recipe count after filtering
                String countString = String.valueOf(i) + " results";
                TextView textView = (TextView) view.findViewById(R.id.recipeCount_textView);
                textView.setText(countString);
            }
        });
    }

}