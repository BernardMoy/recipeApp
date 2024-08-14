package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;

import com.example.recipeapp.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton floatingButton;
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

    // function called by clicking the plus sign floating button
    public void onClick_floatingButton(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.floating_button_popup_menu, popupMenu.getMenu());
        popupMenu.setForceShowIcon(true);
        popupMenu.show();

    }


}