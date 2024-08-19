package com.example.recipeapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import kotlin.UByteArray;

public class AddNewRecipe extends AppCompatActivity {

    // Result launcher for the image picker
    ActivityResultLauncher<Intent> resultLauncher;

    RecyclerView tagsRecyclerView;
    RecyclerView ingredientsRecyclerView;

    // stores current tags that are added
    private ArrayList<String> tagList;

    // stores current list of ingredients
    private ArrayList<Ingredient> ingredientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_recipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // register result for the image picker
        registerResult();

        // set tags to new empty array
        tagList = new ArrayList<>();
        ingredientList = new ArrayList<>();

        // init the recycler views
        tagsRecyclerView = (RecyclerView) findViewById(R.id.recipeTags_recyclerView);
        ingredientsRecyclerView = (RecyclerView) findViewById(R.id.recipeIngredients_recyclerView);
    }

    // Return to previous activity
    public void exitActivity(View v){
       getOnBackPressedDispatcher().onBackPressed();
    }

    // method for the button to get image
    public void getImage(View v){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    // method for the button to clear image
    public void clearImage(View v){
        ImageView recipeImage = (ImageView) findViewById(R.id.recipeImage);
        recipeImage.setImageResource(0);
    }

    // Register result for the image picker
    public void registerResult(){
        ImageView recipeImage = (ImageView) findViewById(R.id.recipeImage);
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        try{
                            Uri image = o.getData().getData();
                            recipeImage.setImageURI(image);

                        } catch (Exception e){
                            // display error message when user does not submit data
                            Toast.makeText(AddNewRecipe.this, "No image selected", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );
    }

    // method for clearing name
    public void clearName(View v){
        TextView textView = (TextView) findViewById(R.id.recipeName_edittext);
        textView.setText("");
    }

    // method for clearing description
    public void clearDesc(View v){
        TextView textView = (TextView) findViewById(R.id.recipeDesc_edittext);
        textView.setText("");
    }

    // method for clearing prep time
    public void clearPrepTime(View v){
        TextView textView = (TextView) findViewById(R.id.recipePrepTime_edittext);
        textView.setText("");
    }

    // method for clearing recipe link
    public void clearLink(View v){
        TextView textView = (TextView) findViewById(R.id.recipeLink_edittext);
        textView.setText("");
    }

    // method for clearing new tag box
    public void clearNewTag(View v){
        TextView textView = (TextView) findViewById(R.id.recipeNewTag_edittext);
        textView.setText("");
    }

    // method for clearing new ingredient
    public void clearNewIngredient(View v){
        TextView ingredientEditText = (TextView) findViewById(R.id.recipeIngredient_edittext);
        TextView amountEditText = (TextView) findViewById(R.id.recipeAmount_edittext);
        TextView supermarketEditText = (TextView) findViewById(R.id.recipeSupermarket_edittext);
        TextView costEditText = (TextView) findViewById(R.id.recipeCost_edittext);
        // reset fields
        ingredientEditText.setText("");
        amountEditText.setText("");
        supermarketEditText.setText("");
        costEditText.setText("");
    }

    // method when the plus button after writing a new tag is pressed
    public void addNewTag(View v){
        TextView textview = (TextView) findViewById(R.id.recipeNewTag_edittext);

        // get the new tag that is submitted
        String newTag = textview.getText().toString();

        // empty tag submitted
        if (newTag.isEmpty()){
            Toast.makeText(AddNewRecipe.this, "Tag is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add tag to arraylist
        tagList.add(newTag);

        // modify the recyclerview of tags that are displayed
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        tagsRecyclerView.setLayoutManager(gridLayoutManager);
        tagsRecyclerView.setAdapter(new StringAdapter(this, tagList));

        // clear box
        textview.setText("");
    }

    // method to add new ingredient row to the table when plus icon is pressed
    public void addNewIngredient(View v){
        // get fields that are submitted
        TextView ingredientEditText = (TextView) findViewById(R.id.recipeIngredient_edittext);
        TextView amountEditText = (TextView) findViewById(R.id.recipeAmount_edittext);
        TextView supermarketEditText = (TextView) findViewById(R.id.recipeSupermarket_edittext);
        TextView costEditText = (TextView) findViewById(R.id.recipeCost_edittext);

        String ingredient = ingredientEditText.getText().toString();
        String amountStr = amountEditText.getText().toString();
        String supermarket = supermarketEditText.getText().toString();
        String costStr = costEditText.getText().toString();

        // empty ingredient name submitted
        if (ingredient.isEmpty()){
            Toast.makeText(AddNewRecipe.this, "Ingredient name is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // empty portion size
        if (amountStr.isEmpty()){
            Toast.makeText(AddNewRecipe.this, "Amount is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // empty supermarket
        if (supermarket.isEmpty()){
            Toast.makeText(AddNewRecipe.this, "Supermarket is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // empty cost
        if (costStr.isEmpty()){
            Toast.makeText(AddNewRecipe.this, "Cost is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // casts
        float amount = Float.parseFloat(amountStr);
        float cost = Float.parseFloat(costStr);

        // construct ingredient and add to arraylist
        Ingredient newIngredient = new Ingredient(ingredient, amount, supermarket, cost);
        ingredientList.add(newIngredient);

        // modify the recycler view that displays list of items
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ingredientsRecyclerView.setLayoutManager(linearLayoutManager);

        IngredientAdapter ingredientRecyclerViewAdapter = new IngredientAdapter(this, ingredientList);
        ingredientsRecyclerView.setAdapter(ingredientRecyclerViewAdapter);

        // reset fields
        ingredientEditText.setText("");
        amountEditText.setText("");
        supermarketEditText.setText("");
        costEditText.setText("");

        updateCost();
    }

    // method to update the cost displayed according to items
    public void updateCost(){

        TextView totalCost = (TextView) findViewById(R.id.totalCost_textView);

        // If no ingredients
        if (ingredientList.isEmpty()){
            String totalCostString = "Total weighted cost: 0";
            totalCost.setText(totalCostString);
        }

        else{  // Else calculate sum
            float total = 0.0f;
            for (Ingredient i : ingredientList){
                total += i.getCost()*i.getAmount();
            }
            String totalCostString = "Total weighted cost: " + String.valueOf(total);
            totalCost.setText(totalCostString);
        }
    }


    // method when the save button is clicked: Add data to database
    public void addRecipeToDatabase(View v){
        String name = ((TextView) findViewById(R.id.recipeName_edittext)).getText().toString();
        ImageView recipeImage = (ImageView) findViewById(R.id.recipeImage);
        String description = ((TextView) findViewById(R.id.recipeDesc_edittext)).getText().toString();
        String link = ((TextView) findViewById(R.id.recipeLink_edittext)).getText().toString();
        String prepTimeString = ((TextView) findViewById(R.id.recipePrepTime_edittext)).getText().toString();
        float prepTime = 0.0f;
        if (!prepTimeString.isEmpty()){
            Log.d("DEBUG", prepTimeString);
            prepTime = Float.parseFloat(prepTimeString);
        }

        // convert image to byte array to be stored as blob
        Bitmap bitmap = ((BitmapDrawable) recipeImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] recipeImageByteArray = byteArrayOutputStream.toByteArray();

        // tags and ingredients are stored in tagList and ingredientList
        DatabaseHelper db = new DatabaseHelper(AddNewRecipe.this);
        long status = db.addRecipe(name, recipeImageByteArray, description, link, prepTime, tagList, ingredientList);

        // exit activity if successful
        // add to database failed
        if (status == -1){
            Toast.makeText(this, "Data adding failed", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "New recipe added", Toast.LENGTH_SHORT).show();
            getOnBackPressedDispatcher().onBackPressed();
        }
    }

    // temp method to reset the database
    public void resetDb(View v){
        DatabaseHelper db = new DatabaseHelper(AddNewRecipe.this);
        db.reset();
    }
}