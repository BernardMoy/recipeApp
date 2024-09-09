package com.example.recipeapp;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddNewMeal extends AppCompatActivity {

    private String dateString;
    private AutoCompleteTextView categoryAutoCompleteTextView;
    private AutoCompleteTextView recipeSuggestionsAutoCompleteTextView;
    private TextView dateTextView;

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

        // load text views
        categoryAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.category_autoCompleteTextView);
        recipeSuggestionsAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.recipeName_autoCompleteTextView);
        dateTextView = (TextView) findViewById(R.id.date_textView);

        if (getIntent().hasExtra("date")){
            dateString = getIntent().getStringExtra("date");
            dateTextView.setText(dbToDisplayDateFormatter(dateString));
        }

        // set up functionality of the auto complete text views
        categoryAutoCompleteTextView = findViewById(R.id.category_autoCompleteTextView);
        recipeSuggestionsAutoCompleteTextView = findViewById(R.id.recipeName_autoCompleteTextView);

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


        // for the recipe name ACTV, get an arraylist of all meal recipe previews.
        ArrayList<MealRecipeSuggestionPreview> previewList = new ArrayList<>();
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(getApplicationContext());
        Cursor cursor = db.getRecipes();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                byte[] image = cursor.getBlob(5);
                String name = cursor.getString(1);
                int id = cursor.getInt(0);

                // get the weighted cost
                Cursor cursor2 = db.getWeightedCost(id);
                cursor2.moveToNext();
                float cost = cursor2.getFloat(0);

                MealRecipeSuggestionPreview mealRecipeSuggestionPreview = new MealRecipeSuggestionPreview(image, name, cost);
                previewList.add(mealRecipeSuggestionPreview);
            }
        }

        // set adapter with the created arraylist
        MealRecipeSuggestionAdapter mealRecipeSuggestionAdapter = new MealRecipeSuggestionAdapter(getApplicationContext(), previewList);
        recipeSuggestionsAutoCompleteTextView.setAdapter(mealRecipeSuggestionAdapter);
        recipeSuggestionsAutoCompleteTextView.setThreshold(1);

        recipeSuggestionsAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    recipeSuggestionsAutoCompleteTextView.showDropDown();
                }
            }
        });

        recipeSuggestionsAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mealRecipeSuggestionAdapter.getFilter().filter(charSequence);
                recipeSuggestionsAutoCompleteTextView.showDropDown();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

    // Return to previous activity
    public void exitActivity(View v){
        getOnBackPressedDispatcher().onBackPressed();
    }

}