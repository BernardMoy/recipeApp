package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.recipeapp.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ImageButton settingsButton;

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

        // start the home fragment at the beginning
        replaceFragment(new HomeFragment());

        // settings button functionality
        settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Settings.class);
                startActivity(i);
            }
        });


    }

    // function to close app
    public void exitApp(View v){
        System.exit(0);
    }

    // function to replace fragment
    protected void replaceFragment(Fragment f){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frameLayout, f);   // replace the frame layout with the fragment provided
        transaction.commit();
    }

    // function to select bottom navbar item programmatically
    // 0,1,2,3 = home, recipe, shoppingList, mealPlanner
    protected void selectBarItem(int i){
        switch (i){
            case 0:
                binding.bottomNavigationView.setSelectedItemId(R.id.home_navbar);
                break;
            case 1:
                binding.bottomNavigationView.setSelectedItemId(R.id.recipes_navbar);
                break;
            case 2:
                binding.bottomNavigationView.setSelectedItemId(R.id.shoppingList_navbar);
                break;
            case 3:
                binding.bottomNavigationView.setSelectedItemId(R.id.mealPlanner_navbar);
                break;
        }
    }

    // function called by clicking the plus sign floating button
    public void onClick_floatingButton(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.floating_button_popup_menu, popupMenu.getMenu());
        popupMenu.setForceShowIcon(true);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.add_recipe_popup){
                    Log.d("PopUp", "Add recipe option clicked");
                    Intent i = new Intent(MainActivity.this, AddNewRecipe.class);
                    i.putExtra("title_text", "New recipe");
                    startActivity(i);
                }
                else if (menuItem.getItemId() == R.id.add_shopping_list_popup){
                    Log.d("PopUp", "Add shopping list option clicked");
                    Intent i = new Intent(MainActivity.this, AddNewShoppingList.class);
                    i.putExtra("title_text", "New shopping list");
                    startActivity(i);
                }
                return true;
            }
        });

    }


    // function to reset the database through the temp delete button
    // temp method to reset the database
    public void resetRecipeDb(View v){
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(this);
        db.reset();
    }

    // function to reset shopping list db
    public void resetShoppingListDb(View v){
        DatabaseHelperShoppingLists db = new DatabaseHelperShoppingLists(this);
        db.reset();
    }
}