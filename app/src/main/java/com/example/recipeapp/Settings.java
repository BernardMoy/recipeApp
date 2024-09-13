package com.example.recipeapp;

import static androidx.core.content.ContextCompat.getDrawable;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity {

    // textviews when clicked on, show dialog
    private TextView clearRecipesTextView;
    private TextView clearMealsTextView;
    private TextView clearShoppingListsTextView;

    private Button cancelButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        clearRecipesTextView = findViewById(R.id.clearRecipes_textView);
        clearMealsTextView = findViewById(R.id.clearMeals_textView);
        clearShoppingListsTextView = findViewById(R.id.clearShoppingLists_textView);

        // on click listeners for clearing data
        clearRecipesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(0);
            }
        });

        clearMealsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(1);
            }
        });

        clearShoppingListsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(2);
            }
        });

    }

    // Return to previous activity
    public void exitActivity(View v){
        getOnBackPressedDispatcher().onBackPressed();
    }

    // method to show dialog. 0, 1, 2 = Recipes, Meals, SL respectively
    public void showDeleteDialog(int num){
        // set up dialog
        Dialog dialog = new Dialog(Settings.this);
        dialog.setContentView(R.layout.confirm_window);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_edit_text, null));
        dialog.setCancelable(false);

        // load the two buttons
        cancelButton = dialog.findViewById(R.id.confirmRecipeCancel_button);
        deleteButton = dialog.findViewById(R.id.confirmRecipeDelete_button);

        // on click listener for the two buttons
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num == 0){
                    DatabaseHelperRecipes db = new DatabaseHelperRecipes(getApplicationContext());
                    db.clearRecipes();
                    db.close();

                } else if (num == 1){
                    DatabaseHelperRecipes db = new DatabaseHelperRecipes(getApplicationContext());
                    db.clearMeals();
                    db.close();

                } else if (num == 2){
                    DatabaseHelperShoppingLists db = new DatabaseHelperShoppingLists(getApplicationContext());
                    db.clearShoppingLists();
                    db.close();

                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}