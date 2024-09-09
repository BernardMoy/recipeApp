package com.example.recipeapp;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddNewMeal extends AppCompatActivity {

    private String dateString;
    private AutoCompleteTextView categoryAutoCompleteTextView;
    private TextView dateTextView;

    private ArrayList<MealRecipeSuggestionPreview> previewList;
    private RecyclerView mealRecipesRecyclerView;
    private MealRecipeSuggestionRecyclerViewAdapter mealRecipeSuggestionRecyclerViewAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_meal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // load text view
        categoryAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.category_autoCompleteTextView);
        dateTextView = (TextView) findViewById(R.id.date_textView);

        if (getIntent().hasExtra("date")) {
            dateString = getIntent().getStringExtra("date");
            dateTextView.setText(dbToDisplayDateFormatter(dateString));
        }

        // set up functionality of the auto complete text view
        categoryAutoCompleteTextView = findViewById(R.id.category_autoCompleteTextView);

        String[] categorySuggestionsList = getResources().getStringArray(R.array.category_suggestions);
        ArrayAdapter<String> arrayAdapterCategory = new ArrayAdapter<>(getApplicationContext(), R.layout.recipe_dropdown_item, categorySuggestionsList);
        categoryAutoCompleteTextView.setAdapter(arrayAdapterCategory);

        // make the suggestions show when the user clicks on the text view
        categoryAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    categoryAutoCompleteTextView.showDropDown();
                }
            }
        });

        categoryAutoCompleteTextView.setThreshold(1); // make it start filtering when 1 character is typed
        categoryAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                arrayAdapterCategory.getFilter().filter(charSequence);
                categoryAutoCompleteTextView.showDropDown();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        // extract recipe data from db
        previewList = new ArrayList<>();
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(AddNewMeal.this);
        Cursor cursor = db.getRecipes();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                int recipeId = cursor.getInt(0);
                String recipeName = cursor.getString(1);
                byte[] recipeImage = cursor.getBlob(5);

                // get the recipe cost
                Cursor cursor2 = db.getWeightedCost(recipeId);
                cursor2.moveToNext();
                float cost = cursor2.getFloat(0);

                MealRecipeSuggestionPreview mealRecipeSuggestionPreview = new MealRecipeSuggestionPreview(recipeId, recipeImage, recipeName, cost);

                previewList.add(mealRecipeSuggestionPreview);
            }
        }

        // set up recyclerview to display recipes
        mealRecipesRecyclerView = findViewById(R.id.mealRecipes_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddNewMeal.this, LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        mealRecipesRecyclerView.setLayoutManager(linearLayoutManager);

        mealRecipeSuggestionRecyclerViewAdapter = new MealRecipeSuggestionRecyclerViewAdapter(AddNewMeal.this, previewList, dateString);
        mealRecipesRecyclerView.setAdapter(mealRecipeSuggestionRecyclerViewAdapter);


        // set up search view
        searchView = findViewById(R.id.mealRecipes_searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Filter filter = mealRecipeSuggestionRecyclerViewAdapter.getFilter();
                filter.filter(s);
                return false;
            }
        });

        // display empty message
        TextView emptyTextView = findViewById(R.id.emptyMealRecipes_textView);
        if (!previewList.isEmpty()){
            emptyTextView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
        }
    }

    public String dbToDisplayDateFormatter(String dateStr){
        SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // parse string to date object
        try{
            Date date = sourceDateFormat.parse(dateStr);
            SimpleDateFormat targetDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");

            if (date != null){
                return targetDateFormat.format(date);
            } else {
                return "Invalid date";
            }

        } catch (ParseException e){
            return "Invalid date";
        }
    }

    public void clearCategory(View v){
        AutoCompleteTextView tv = (AutoCompleteTextView) findViewById(R.id.category_autoCompleteTextView);
        tv.setText("");
    }

    // Return to previous activity
    public void exitActivity(View v){
        getOnBackPressedDispatcher().onBackPressed();
    }

}