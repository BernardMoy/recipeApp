package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.recipeapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // start fragment
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int selectedItem = item.getItemId();

            if (selectedItem == R.id.home_navbar){
                replaceFragment(new HomeFragment());
            }
            else if (selectedItem == R.id.recipes_navbar){
                replaceFragment(new RecipeFragment());
            }
            else if (selectedItem == R.id.shoppingList_navbar){
                replaceFragment(new ShoppingListFragment());
            }
            else if (selectedItem == R.id.mealPlanner_navbar){
                replaceFragment(new MealPlannerFragment());
            }
            return true;
        });


    }

    // function to replace fragment
    private void replaceFragment(Fragment f){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frameLayout, f);   // replace the frame layout with the fragment provided
        transaction.commit();
    }
}