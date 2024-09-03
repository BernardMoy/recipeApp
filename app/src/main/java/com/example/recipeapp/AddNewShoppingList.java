package com.example.recipeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    // the big recyclerview (Supermarket recyclerview)
    private RecyclerView supermarketsRecyclerView;

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

        // load the recycler view
        supermarketsRecyclerView = findViewById(R.id.shoppingListSupermarkets_recyclerView);
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

        String supermarket = ingredientSupermarketEditText.getText().toString();

        String ingredientName = ingredientNameEditText.getText().toString();
        String amountStr = ingredientAmountEditText.getText().toString();
        String costStr = ingredientCostEditText.getText().toString();


        // check if ingredient name is null
        if (ingredientName.isEmpty()){
            Toast.makeText(AddNewShoppingList.this, "Ingredient name is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // empty portion size and type cast
        int amount = 0;
        if (!amountStr.isEmpty()){
            amount = Integer.parseInt(amountStr);
        }

        // empty cost
        float cost = 0.0f;
        if (!costStr.isEmpty()){
            cost = Float.parseFloat(costStr);
            cost = Math.round(cost * 100) / 100.0f;  // trim to 2 dp
        }


        // create new shopping list ingredient
        ShoppingListIngredient ingredient = new ShoppingListIngredient(ingredientName, amount, cost);

        // Modify the hashmap based on whether same supermarket exists
        if (shoppingListIngredientsHashMap.containsKey(supermarket)){
            // add to existing key
            shoppingListIngredientsHashMap.get(supermarket).add(ingredient);

        } else {
            // create new key
            ArrayList<ShoppingListIngredient> newList = new ArrayList<>();
            newList.add(ingredient);
            shoppingListIngredientsHashMap.put(supermarket, newList);
        }

        // set adapter for the supermarket recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddNewShoppingList.this, LinearLayoutManager.VERTICAL, false);
        supermarketsRecyclerView.setLayoutManager(linearLayoutManager);

        ShoppingListSupermarketAdapter supermarketAdapter = new ShoppingListSupermarketAdapter(AddNewShoppingList.this, shoppingListIngredientsHashMap);  // supply the hash map here
        supermarketsRecyclerView.setAdapter(supermarketAdapter);

        // reset fields
        ingredientNameEditText.setText("");
        ingredientAmountEditText.setText("");
        ingredientCostEditText.setText("");
    }

    // Return to previous activity
    public void exitActivity(View v){
        getOnBackPressedDispatcher().onBackPressed();
    }

    // when the done button is clicked
    public void updateShoppingListToDatabase(View v){
        // check shopping list name is null
        String shoppingListName = nameEditText.getText().toString();
        if (shoppingListName.isEmpty()){
            Toast.makeText(AddNewShoppingList.this, "Shopping list name is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String shoppingListDescription = descriptionEditText.getText().toString();

    }
}