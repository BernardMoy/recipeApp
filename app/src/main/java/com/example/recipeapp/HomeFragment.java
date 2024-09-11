package com.example.recipeapp;

import static android.text.TextUtils.replace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
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
    private ImageButton recipeArrowImageButton;
    private ImageButton shoppingListArrowImageButton;
    private ImageButton mealPlannerArrowImageButton;

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
        recipeArrowImageButton = view.findViewById(R.id.recipeArrow_imageButton);
        shoppingListArrowImageButton = view.findViewById(R.id.shoppingListsArrow_imageButton);
        mealPlannerArrowImageButton = view.findViewById(R.id.mealPlannerArrow_imageButton);

        recipeArrowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).selectBarItem(1);
                ((MainActivity) getActivity()).replaceFragment(new RecipeFragment());
            }
        });

        shoppingListArrowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).selectBarItem(2);
                ((MainActivity) getActivity()).replaceFragment(new ShoppingListFragment());
            }
        });

        mealPlannerArrowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).selectBarItem(3);
                ((MainActivity) getActivity()).replaceFragment(new MealPlannerFragment());
            }
        });

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

        Cursor cursor = db.getRecipes();
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
            return true;

        } else {

            // there are no recipes
            generatedRecipeId = -1;

            emptyRecipeTextView.setVisibility(View.VISIBLE);
            generateRecipeConstraintLayout.setVisibility(View.GONE);
            generateRecipeConstraintLayout.setEnabled(false);

            return false;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        loadRecipeData();
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

        String prepTimeStr = " " + String.valueOf(prepTime) + " minutes";
        recipePrepTimeTextView.setText(prepTimeStr);

        recipeCostTextView.setText(String.valueOf(cost));

        String timesCookedStr = " " + String.valueOf(timesCooked) + " cooked";
        recipeTimesCookedTextView.setText(timesCookedStr);

        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        recipeImageView.setImageBitmap(bitmap);

    }
}