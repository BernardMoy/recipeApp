package com.example.recipeapp;

import static androidx.core.content.ContextCompat.getDrawable;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MealPlannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MealPlannerFragment extends Fragment {

    // main calendarview
    private CalendarView calendarView;
    private Calendar calendar;
    private Context ctx;

    // recyclerview for meals
    private RecyclerView mealsRecyclerView;
    private ArrayList<MealPreview> mealPreviewList;

    // add button
    private ImageButton addMealButton;
    private Button cancelButton;
    private Button createButton;

    // stores the current selected date
    private String dateString;

    private TextView emptyMealTextView;


    // store a list of date strings that are selected
    private HashSet<String> selectedDateStringSet;
    private ToggleButton dateCheckToggleButton;
    private TextView selectedCountTextView;
    private ConstraintLayout selectedOptionsConstraintLayout;

    private ImageButton deselectButton;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MealPlannerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MealPlannerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MealPlannerFragment newInstance(String param1, String param2) {
        MealPlannerFragment fragment = new MealPlannerFragment();
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
        View view = inflater.inflate(R.layout.fragment_meal_planner, container, false);
        ctx = view.getContext();

        // find calendarview
        calendarView = view.findViewById(R.id.calendarView);
        calendar = Calendar.getInstance();

        // load the selected set
        selectedDateStringSet = new HashSet<>();
        selectedCountTextView = view.findViewById(R.id.selectedCount_textView);

        // load the top bar
        selectedOptionsConstraintLayout = view.findViewById(R.id.selectedOptions_constraintLayout);
        selectedOptionsConstraintLayout.setVisibility(View.GONE);   // initially gone

        // load the toggle button
        dateCheckToggleButton = view.findViewById(R.id.dateCheck_toggleButton);

        // load the empty text view
        emptyMealTextView = view.findViewById(R.id.emptyMeal_textView);

        // find recyclerview
        mealsRecyclerView = view.findViewById(R.id.meals_recyclerView);
        mealPreviewList = new ArrayList<>();

        // get the current month and day, then call the listener method
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        dayChange(view, currentYear, currentMonth, currentDay);

        // listener when a new date is selected
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                dayChange(view, year, month, day);
            }
        });

        // listener for the add button
        addMealButton = view.findViewById(R.id.addMeal_imageButton);
        addMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open add meal activity
                Intent intent = new Intent(ctx, AddNewMeal.class);
                intent.putExtra("date", dateString);

                startActivity(intent);
            }
        });

        // set up toggle button
        dateCheckToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    // add current date to hashset
                    selectedDateStringSet.add(dateString);
                    updateSelectedCount();

                } else {
                    selectedDateStringSet.remove(dateString);
                    updateSelectedCount();
                }
            }
        });

        // deselect button functionality
        deselectButton = view.findViewById(R.id.deselect_button);
        deselectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reset the hashset
                selectedDateStringSet.clear();
                updateSelectedCount();
                updateCheckButton();
            }
        });

        return view;
    }

    // method called when the date and month is updated. This is called initially when loading the fragment
    public void dayChange(View view, int year, int month, int day){
        // set global date string variable
        dateString = String.format("%04d-%02d-%02d", year, month+1, day);  // month starts at 0

        // set the date string
        TextView dateTextView = view.findViewById(R.id.date_textView);
        dateTextView.setText(dbToDisplayDateFormatter(dateString));

        // load recyclerview for the meals on that day
        updateMealRecyclerView();

        updateCheckButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMealRecyclerView();
    }

    /*
    method to update the recyclerview displayed below the calendar.
    When given a date, all the meals of that date is extracted.
    This method is called when a date is changed, or onResume() such as exiting from creating a new meal.

    Remember to modify the date string (Global variable) before calling this method.
     */
    public void updateMealRecyclerView(){
        mealPreviewList = new ArrayList<>();

        // load the arraylist from db
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);
        Cursor cursor = db.getMealsFromDate(dateString);
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                int mealId = cursor.getInt(0);
                String category = cursor.getString(1);
                String recipeName = cursor.getString(2);
                byte[] recipeImage = cursor.getBlob(3);
                int recipeId = cursor.getInt(4);

                // find the cost of that recipe id
                Cursor cursor2 = db.getWeightedCost(recipeId);
                cursor2.moveToNext();
                float recipeCost = cursor2.getFloat(0);

                // construct object
                MealPreview mealPreview = new MealPreview(mealId, category, recipeName, recipeImage, recipeCost);
                mealPreviewList.add(mealPreview);
            }
        }

        // set up recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        mealsRecyclerView.setLayoutManager(linearLayoutManager);
        MealAdapter mealAdapter = new MealAdapter(ctx, mealPreviewList);
        mealsRecyclerView.setAdapter(mealAdapter);

        // show or hide the empty message
        if (mealPreviewList.isEmpty()){
            emptyMealTextView.setVisibility(View.VISIBLE);
        } else {
            emptyMealTextView.setVisibility(View.GONE);
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

    // update the selected count textview
    public void updateSelectedCount(){
        String s = String.valueOf(selectedDateStringSet.size()) + " selected";
        selectedCountTextView.setText(s);

        // if count > 0, show the constraint layout, hide otherwise
        if (selectedDateStringSet.isEmpty()){
            selectedOptionsConstraintLayout.setVisibility(View.GONE);

        } else {
            selectedOptionsConstraintLayout.setVisibility(View.VISIBLE);

        }
    }

    // update the toggle button shown. this is called when dayChange() is called, or when the deselect button is pressed
    public void updateCheckButton(){
        // update the toggle button according to whether the date is in the selected set
        dateCheckToggleButton.setChecked(selectedDateStringSet.contains(dateString));
    }
}
