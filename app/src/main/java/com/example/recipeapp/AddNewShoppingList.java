package com.example.recipeapp;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class AddNewShoppingList extends AppCompatActivity {

    // Textviews for handling user inputs
    private TextView nameEditText;
    private TextView descriptionEditText;
    private TextView ingredientNameEditText;
    private TextView ingredientAmountEditText;
    private TextView ingredientCostEditText;
    private AutoCompleteTextView ingredientSupermarketAutoCompleteTextView;

    // hashmap for storing ingredients classified by supermarkets.
    private LinkedHashMap<String, ArrayList<ShoppingListIngredient>> shoppingListIngredientsHashMap;

    // the big recyclerview (Supermarket recyclerview)
    private RecyclerView supermarketsRecyclerView;

    /*
    Again, same activity is used for edit and create new SL.

    If recipeId == -1, it is in "Creating new SL" mode, otherwise it is updating/editing recipe.
     */
    private int shoppingListId;

    private HashSet<String> currentSupermarketSet;
    

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
        ingredientSupermarketAutoCompleteTextView = findViewById(R.id.shoppingListSupermarket_autoCompleteTextView);

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

            // 1. Get basic information from the database
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

            // 2. Get supermarket ing hashmap from the database, and update the hashmap
            Cursor cursor2 = db.getShoppingListSupermarketIngredientsFromId(shoppingListId);
            if (cursor2.getCount() > 0){
                while (cursor2.moveToNext()){
                    String supermarket = cursor2.getString(0);
                    String ingredientName = cursor2.getString(1);
                    int ingredientAmount = cursor2.getInt(2);
                    float ingredientCost = cursor2.getFloat(3);
                    boolean isChecked = (cursor2.getInt(4) == 1);

                    // create new ingredient object
                    ShoppingListIngredient ingredient = new ShoppingListIngredient(ingredientName, ingredientAmount, ingredientCost, isChecked);

                    // add supermarket info to hashmap
                    addToHashMap(supermarket, ingredient);
                }
            }
            db.close();

            // 3. Load the recyclerview from hashmap
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddNewShoppingList.this, LinearLayoutManager.VERTICAL, false);
            supermarketsRecyclerView.setLayoutManager(linearLayoutManager);

            ShoppingListSupermarketAdapter supermarketAdapter = new ShoppingListSupermarketAdapter(AddNewShoppingList.this, shoppingListIngredientsHashMap);  // supply the hash map here
            supermarketsRecyclerView.setAdapter(supermarketAdapter);

        }

        // set up exit button
        ImageButton exitButton = findViewById(R.id.exit_imageButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set up dialog
                Dialog dialog = new Dialog(AddNewShoppingList.this);
                dialog.setContentView(R.layout.confirm_unsaved_changes_window);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.custom_edit_text, null));
                dialog.setCancelable(true);

                // load the two buttons
                Button confirmCancelButton = dialog.findViewById(R.id.confirmCancel_button);
                Button confirmExitButton = dialog.findViewById(R.id.confirmExit_button);

                confirmCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                confirmExitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        getOnBackPressedDispatcher().onBackPressed();
                    }
                });

                dialog.show();
            }
        });


        // set up the ingredient supermarket auto complete text view
        // first, load all supermarket names from the db to the set
        currentSupermarketSet = new HashSet<>();

        DatabaseHelperShoppingLists db = new DatabaseHelperShoppingLists(getApplicationContext());
        Cursor cursor = db.getDistinctSupermarkets();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                currentSupermarketSet.add(cursor.getString(0));
            }
        }
        String[] currentSupermarketSetStrings = currentSupermarketSet.toArray(new String[]{});
        ArrayAdapter<String> arrayAdapterSupermarket = new ArrayAdapter<>(AddNewShoppingList.this, R.layout.recipe_dropdown_item, currentSupermarketSetStrings);
        ingredientSupermarketAutoCompleteTextView.setAdapter(arrayAdapterSupermarket);

        ingredientSupermarketAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    ingredientSupermarketAutoCompleteTextView.showDropDown();
                }
            }
        });
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
        ingredientSupermarketAutoCompleteTextView.setText("");
    }

    // method to add new ingredient - sort by supermarkets
    public void addNewIngredient(View v){

        String supermarket = ingredientSupermarketAutoCompleteTextView.getText().toString();
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
        /*
        -1 is the placeholder for the ingredientID here. The id is necessary because it is used to mark ing as checked / unchecked.
        It will be replaced by the real ingredient ID when extracted from the db.
         */
        ShoppingListIngredient ingredient = new ShoppingListIngredient(ingredientName, amount, cost, false);
        addToHashMap(supermarket, ingredient);

        // set adapter for the supermarket recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddNewShoppingList.this, LinearLayoutManager.VERTICAL, false);
        supermarketsRecyclerView.setLayoutManager(linearLayoutManager);

        ShoppingListSupermarketAdapter supermarketAdapter = new ShoppingListSupermarketAdapter(AddNewShoppingList.this, shoppingListIngredientsHashMap);  // supply the hash map here
        supermarketsRecyclerView.setAdapter(supermarketAdapter);

        // add the supermarket name to the currently stored hashmap
        currentSupermarketSet.add(supermarket);

        // reset fields
        ingredientNameEditText.setText("");
        ingredientAmountEditText.setText("");
        ingredientCostEditText.setText("");
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

        // add or update SL to database
        DatabaseHelperShoppingLists db = new DatabaseHelperShoppingLists(this);
        if (shoppingListId == -1){
            boolean status = db.addShoppingList(shoppingListName, shoppingListDescription, shoppingListIngredientsHashMap);

            // exit activity if successful
            if (!status){
                Toast.makeText(this, "Data adding failed", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "New shopping list added", Toast.LENGTH_SHORT).show();
                getOnBackPressedDispatcher().onBackPressed();
            }

        } else {
            boolean status = db.updateShoppingListFromId(shoppingListId, shoppingListName, shoppingListDescription, shoppingListIngredientsHashMap);

            // exit activity if successful
            if (!status){
                Toast.makeText(this, "Data editing failed", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Shopping list saved", Toast.LENGTH_SHORT).show();
                getOnBackPressedDispatcher().onBackPressed();
            }

        }

        db.close();
    }

    // method to add to hashmap when given a supermarket and ingredient
    public void addToHashMap(String supermarket, ShoppingListIngredient ingredient){

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
    }
}