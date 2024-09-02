package com.example.recipeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class AddNewShoppingList extends AppCompatActivity {

    // Textviews for handling user inputs
    private TextView nameEditText;
    private TextView descriptionEditText;
    private TextView ingredientNameEditText;
    private TextView ingredientAmountEditText;
    private TextView ingredientCostEditText;
    private TextView ingredientSupermarketEditText;

    // hashmap for storing ingredients classified by supermarkets.
    private HashMap<String, ArrayList<ShoppingListIngredient>> shoppingListIngredientsHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_shopping_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // load the text views for handling user inputs
        nameEditText = findViewById(R.id.shoppingListName_edittext);
        descriptionEditText = findViewById(R.id.shoppingListDesc_edittext);
        ingredientNameEditText = findViewById(R.id.shoppingListIngredient_edittext);
        ingredientAmountEditText = findViewById(R.id.shoppingListAmount_edittext);
        ingredientCostEditText = findViewById(R.id.shoppingListCost_edittext);
        ingredientSupermarketEditText = findViewById(R.id.shoppingListSupermarket_edittext);

        // load the hashmap
        shoppingListIngredientsHashMap = new HashMap<>();
    }

    // methods for the inputs
    public void clearName(View v){
        nameEditText.setText("");
    }

    public void clearDesc(View v){
        descriptionEditText.setText("");
    }

    public void clearNewIngredient(View v){
        ingredientNameEditText.setText("");
        ingredientAmountEditText.setText("");
        ingredientCostEditText.setText("");
        ingredientSupermarketEditText.setText("");
    }

    // method to add new ingredient - sort by supermarkets
    public void addNewIngredient(View v){

    }

    // Return to previous activity
    public void exitActivity(View v){
        getOnBackPressedDispatcher().onBackPressed();
    }

    // when the done button is clicked
    public void updateShoppingListToDatabase(View v){

    }
}