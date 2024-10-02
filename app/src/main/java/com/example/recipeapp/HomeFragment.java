package com.example.recipeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private Context ctx;

    // the constraint layout of the random recipe shown
    private ConstraintLayout generateRecipeConstraintLayout;

    // the refresh button to generate a random recipe
    private ImageButton refreshButton;

    // empty recipe textview
    private TextView emptyRecipeTextView;

    // the views used to display the generated recipe
    private int generatedRecipeId;
    private ImageView recipeImageView;
    private TextView recipeNameTextView;
    private TextView recipePrepTimeTextView;
    private TextView recipeCostTextView;
    private TextView recipeTimesCookedTextView;

    // the 3 arrow buttons that direct to different fragment
    private ConstraintLayout recipesConstraintLayout;
    private ConstraintLayout shoppingListsConstraintLayout;
    private ConstraintLayout mealPlannerConstraintLayout;

    // greeting textview that changes over time
    private TextView greetingTextView;

    // textviews for analytics
    private TextView recipeAddedTextView;
    private TextView spent7TextView;
    private TextView spent30TextView;

    // textviews for the latest shopping list
    private ConstraintLayout lastShoppingListConstraintLayout;
    private TextView lastShoppingListName;
    private TextView lastShoppingListItemCount;
    private TextView lastShoppingListPlaceCount;
    private TextView lastShoppingListCost;
    private TextView lastShoppingListStatus;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ctx = view.getContext();

        // load the refresh button
        refreshButton = view.findViewById(R.id.refresh_imageButton);

        // load constraint layout
        generateRecipeConstraintLayout = view.findViewById(R.id.generateRecipeConstraintLayout);

        // load the empty text view
        emptyRecipeTextView = view.findViewById(R.id.emptyGenerateRecipes_textView);

        // load views for the generated recipe
        recipeImageView = view.findViewById(R.id.generateRow_image);
        recipeNameTextView = view.findViewById(R.id.generateRow_name);
        recipePrepTimeTextView = view.findViewById(R.id.generateRow_prepTime);
        recipeCostTextView = view.findViewById(R.id.generateRow_cost);
        recipeTimesCookedTextView = view.findViewById(R.id.generateRow_timesCooked);

        // load views for the latest shopping list
        lastShoppingListConstraintLayout = view.findViewById(R.id.lastShoppingList_ConstraintLayout);
        lastShoppingListName = view.findViewById(R.id.lastShoppingList_name);
        lastShoppingListItemCount = view.findViewById(R.id.lastShoppingList_itemCount);
        lastShoppingListPlaceCount = view.findViewById(R.id.lastShoppingList_placeCount);
        lastShoppingListCost = view.findViewById(R.id.lastShoppingList_cost);
        lastShoppingListStatus = view.findViewById(R.id.lastShoppingList_status);

        // set on click listener
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRandomRecipe();
            }
        });

        // set click listener of the constraint layout --> Edit recipe
        generateRecipeConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, AddNewRecipe.class);
                intent.putExtra("title_text", "Edit recipe");
                intent.putExtra("recipe_id", generatedRecipeId);

                startActivity(intent);
            }
        });

        // generate the first recipe
        generateRandomRecipe();

        // load the arrow image buttons
        recipesConstraintLayout = view.findViewById(R.id.recipes_constraintLayout);
        shoppingListsConstraintLayout = view.findViewById(R.id.shoppingLists_constraintLayout);
        mealPlannerConstraintLayout = view.findViewById(R.id.mealPlanner_constraintLayout);

        recipesConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).selectBarItem(1);
                ((MainActivity) getActivity()).replaceFragment(new RecipeFragment());
            }
        });

        shoppingListsConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).selectBarItem(2);
                ((MainActivity) getActivity()).replaceFragment(new ShoppingListFragment());
            }
        });

        mealPlannerConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).selectBarItem(3);
                ((MainActivity) getActivity()).replaceFragment(new MealPlannerFragment());
            }
        });


        // load the greeting textview by the current time
        greetingTextView = view.findViewById(R.id.greeting_textView);

        LocalTime time = LocalTime.now();
        int hour = time.getHour();

        if (hour < 5 || hour >= 18){
            greetingTextView.setText("Good evening!");

        } else if (hour >= 12) {
            greetingTextView.setText("Good afternoon!");
        }


        // load the textviews for analytics
        recipeAddedTextView = view.findViewById(R.id.recipesAdded_textView);
        spent7TextView = view.findViewById(R.id.spent7_textView);
        spent30TextView = view.findViewById(R.id.spent30_textView);
        loadAnalytics();

        loadLastShoppingList();


        return view;
    }

    /**
     * generates a random recipe and modify the textviews that are displayed.
     *
     * @return true if sucessfully generated, false if there are no recipes.
     */
    public boolean generateRandomRecipe(){

        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);

        // from db, get a list of all recipe ids
        ArrayList<Integer> recipeIdList = new ArrayList<>();

        Cursor cursor = db.getRecipes(0);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int recipeId = cursor.getInt(0);
                recipeIdList.add(recipeId);
            }

            // generate a random recipe id and change global var
            Random random = new Random();
            int randomIndex = random.nextInt(recipeIdList.size());
            generatedRecipeId = recipeIdList.get(randomIndex);

            loadRecipeData();

            // set empty text views
            emptyRecipeTextView.setVisibility(View.GONE);
            generateRecipeConstraintLayout.setVisibility(View.VISIBLE);
            generateRecipeConstraintLayout.setEnabled(true);

            db.close();
            return true;

        } else {

            // there are no recipes
            generatedRecipeId = -1;

            emptyRecipeTextView.setVisibility(View.VISIBLE);
            generateRecipeConstraintLayout.setVisibility(View.GONE);
            generateRecipeConstraintLayout.setEnabled(false);

            db.close();
            return false;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        loadRecipeData();
        loadLastShoppingList();
        loadAnalytics();
    }

    /*
    load recipe data from the generated recipe. Does not do anything if the id is -1.
    Called on onResume and also immediately after generated recipe
     */
    public void loadRecipeData(){
        if (generatedRecipeId == -1) {
            return;
        }

        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);

        // get info for that rid
        Cursor cursor1 = db.getRecipeFromId(generatedRecipeId);
        // if that recipe id does not exist, skip
        if (cursor1.getCount() > 0){
            cursor1.moveToNext();

            String name = cursor1.getString(0);
            float prepTime = cursor1.getFloat(4);
            int timesCooked = cursor1.getInt(5);
            byte[] image = cursor1.getBlob(1);

            // get the weighted cost
            Cursor cursor2 = db.getWeightedCost(generatedRecipeId);
            cursor2.moveToNext();
            float cost = cursor2.getFloat(0);

            // update shown parameters
            recipeNameTextView.setText(name);

            String prepTimeStr = " " + String.valueOf(prepTime) + " mins";
            recipePrepTimeTextView.setText(prepTimeStr);

            recipeCostTextView.setText(String.valueOf(cost));

            String timesCookedStr = " " + String.valueOf(timesCooked) + " cooked";
            recipeTimesCookedTextView.setText(timesCookedStr);

            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            recipeImageView.setImageBitmap(bitmap);

        } else {
            // in this case, the original recipe id is not found (prob due to the recipes being deleted from settings)
            generateRandomRecipe();
        }

        db.close();

    }

    public void loadAnalytics(){
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat1.format(calendar1.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String days7ago = dateFormat.format(calendar.getTime());

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_MONTH, -30);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        String days30ago = dateFormat2.format(calendar2.getTime());

        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);

        // get the meals added
        Cursor cursor = db.getMealCount();
        cursor.moveToNext();
        int mealCount = cursor.getInt(0);

        Cursor cursor1 = db.getCostAfterDate(days7ago, currentDate);
        cursor1.moveToNext();
        float costSeven = cursor1.getFloat(0);

        Cursor cursor2 = db.getCostAfterDate(days30ago, currentDate);
        cursor2.moveToNext();
        float costThirty = cursor2.getFloat(0);

        db.close();

        // modify text view

        recipeAddedTextView.setText(String.valueOf(mealCount));
        spent7TextView.setText(String.valueOf(costSeven));
        spent30TextView.setText(String.valueOf(costThirty));
    }

    private void loadLastShoppingList(){
        // load the latest viewed shopping list
        // get that info from sharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("homePageInfo", Context.MODE_PRIVATE);
        int lastShoppingListId = sharedPreferences.getInt("lastShoppingListId", -1);

        // load data
        boolean success = loadLastShoppingListFromDb(lastShoppingListId);

        if (!success){
            // if that id cannot be found or no SL, set view to gone
            lastShoppingListConstraintLayout.setVisibility(View.GONE);
            lastShoppingListConstraintLayout.setEnabled(false);

        } else {
            // success
            lastShoppingListConstraintLayout.setEnabled(true);
            lastShoppingListConstraintLayout.setVisibility(View.VISIBLE);

            // set a on click listener for that area
            lastShoppingListConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, AddNewShoppingList.class);
                    intent.putExtra("title_text", "Edit shopping list");
                    intent.putExtra("shopping_list_id", lastShoppingListId);

                    startActivity(intent);
                }
            });
        }
    }

    // method to load shopping list data to the last viewed SL from db. Return true if that id is valid.
    public boolean loadLastShoppingListFromDb(int id){
        DatabaseHelperShoppingLists db = new DatabaseHelperShoppingLists(ctx);

        Cursor cursor = db.getShoppingListFromId(id);
        if (cursor.getCount() > 0){
            cursor.moveToNext();
            lastShoppingListName.setText(cursor.getString(0));

        } else {
            db.close();
            return false;
        }

        Cursor cursor1 = db.getShoppingListsNumsFromId(id);
        if (cursor1.getCount() > 0){
            cursor1.moveToNext();
            String itemCountString = String.valueOf(cursor1.getInt(0)) + " items";
            String placeCountString = "(" + String.valueOf(cursor1.getInt(1)) + " places)";
            String costString = String.valueOf(cursor1.getFloat(2));

            lastShoppingListItemCount.setText(itemCountString);
            lastShoppingListPlaceCount.setText(placeCountString);
            lastShoppingListCost.setText(costString);


            // display the percentage of SL that is completed
            // first get count of the ingredients
            Cursor cursor2 = db.getShoppingListsNumsFromId(id);
            cursor2.moveToNext();

            // if empty, display empty message
            if (cursor2.getInt(0) == 0){
                String text = "  -";
                lastShoppingListStatus.setText(text);
                lastShoppingListStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.circle_icon_gray, 0, 0, 0);

            } else {
                // get the percentage
                Cursor cursor3 = db.getShoppingListPercentage(id);
                cursor3.moveToNext();
                int percentage = Math.round(cursor3.getFloat(0)*100);
                String text = "  " + String.valueOf(percentage) + "%";
                lastShoppingListStatus.setText(text);

                if (percentage == 0){
                    lastShoppingListStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.circle_icon_red, 0, 0, 0);

                } else if (percentage == 100){
                    lastShoppingListStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.circle_icon_green, 0, 0, 0);

                } else {
                    lastShoppingListStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.circle_icon_orange, 0, 0, 0);
                }
            }

            db.close();

        } else {
            db.close();
            return false;
        }

        db.close();
        return true;
    }
}