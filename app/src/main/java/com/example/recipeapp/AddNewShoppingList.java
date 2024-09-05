package com.example.recipeapp;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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
import java.util.LinkedHashMap;

public class AddNewShoppingList extends AppCompatActivity {

    // Textviews for handling user inputs
    private TextView nameEditText;
    private TextView descriptionEditText;
    private TextView ingredientNameEditText;
    private TextView ingredientAmountEditText;
    private TextView ingredientCostEditText;
    private TextView ingredientSupermarketEditText;

    // hashmap for storing ingredients classified by supermarkets.
    private LinkedHashMap<String, ArrayList<ShoppingListIngredient>> shoppingListIngredientsHashMap;

    // the big recyclerview (Supermarket recyclerview)
    private RecyclerView supermarketsRecyclerView;

    /*
    Again, same activity is used for edit and create new SL.

    If recipeId == -1, it is in "Creating new SL" mode, otherwise it is updating/editing recipe.
     */
    private int shoppingListId;
    

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
        shoppingListIngredientsHashMap = new LinkedHashMap<>();

        // load the recycler view
        supermarketsRecyclerView = findViewById(R.id.shoppingListSupermarkets_recyclerView);

        // Get the recipe title text passed
        if (getIntent().hasExtra("title_text")) {
            String titleText = getIntent().getStringExtra("title_text");
            TextView titleTextTextView = (TextView) findViewById(R.id.shoppingList_titleText);
            titleTextTextView.setText(titleText);

        } else {
            Toast.makeText(getApplicationContext(), "Recipe did not load properly", Toast.LENGTH_SHORT).show();
        }

        // set the initial value of shoppingList id to -1
        shoppingListId = -1;

        // Get the shoppingList id passed, if it exists
        if (getIntent().hasExtra("shopping_list_id")) {
            shoppingListId = getIntent().getIntExtra("shopping_list_id", -1);

            // Get information from the database
            DatabaseHelperShoppingLists db = new DatabaseHelperShoppingLists(getApplicationContext());
            Cursor cursor = db.getShoppingListFromId(shoppingListId);


            if (cursor.getCount() > 0) {
                cursor.moveToNext();

                String name = cursor.getString(0);
                String desc = cursor.getString(1);

                TextView nameEditText = (TextView) findViewById(R.id.shoppingListName_edittext);
                nameEditText.setText(name);

                TextView descEditText = (TextView) findViewById(R.id.shoppingListDesc_edittext);
                descEditText.setText(desc);
            }
        }
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
        int amount = 1;
        if (!amountStr.isEmpty()){
            amount = Integer.parseInt(amountStr);

            // check if amount is greater or equal 1
            if (amount < 1){
                Toast.makeText(AddNewShoppingList.this, "Amount has to be at least 1", Toast.LENGTH_SHORT).show();
                return;
            }
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

        // add SL to database
        DatabaseHelperShoppingLists db = new DatabaseHelperShoppingLists(this);
        boolean status = db.addShoppingList(shoppingListName, shoppingListDescription, shoppingListIngredientsHashMap);

        // exit activity if successful
        if (!status){
            Toast.makeText(this, "Data adding failed", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "New shopping list added", Toast.LENGTH_SHORT).show();
            getOnBackPressedDispatcher().onBackPressed();
        }
    }
}