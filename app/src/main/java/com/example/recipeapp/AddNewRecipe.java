package com.example.recipeapp;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class AddNewRecipe extends AppCompatActivity {

    // Result launcher for the image picker
    ActivityResultLauncher<Intent> resultLauncher;

    private RecyclerView tagsRecyclerView;
    private RecyclerView ingredientsRecyclerView;

    // stores current tags that are added
    private ArrayList<String> tagList;

    // stores current list of ingredients
    private ArrayList<Ingredient> ingredientList;

    // store the recipe title text
    private String recipeTitle;

    /*
    Note that the same activity (AddNewRecipe) is used to creating new recipe and editing a recipe.
    RecipeId is default to -1, and if an intent (recipe_id) is passed, it will be set to the corresponding recipe id.

    If recipeId == -1, it is in "Creating new recipe" mode, otherwise it is updating/editing recipe.
     */
    private int recipeId;

    // the edittext for ingredient name
    private TextView ingredientEditText;
    private TextView amountEditText;
    private AutoCompleteTextView supermarketEditText;
    private TextView costEditText;
    private TextView shelfLifeEditText;

    // the linearlayout for displaying ingredient suggestions
    private LinearLayout ingredientSuggestionLinearLayout;

    // the adapter for displaying ing suggestions
    private RecyclerView ingredientSuggestionRecyclerView;
    private IngredientSuggestionRecyclerViewAdapter ingredientSuggestionRecyclerViewAdapter;

    // store the ingredient input fields. Passed to the ing suggestion adapter to modify values
    private LinearLayout ingredientFieldsLinearLayout;

    // link redirect icon
    private ImageButton redirectImageButton;

    // stores currently added supermarket names
    private HashSet<String> currentSupermarketSet;

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

        // set tags and ingredients to new empty array
        tagList = new ArrayList<>();
        ingredientList = new ArrayList<>();

        // init the recycler views
        tagsRecyclerView = (RecyclerView) findViewById(R.id.recipeTags_recyclerView);
        ingredientsRecyclerView = (RecyclerView) findViewById(R.id.recipeIngredients_recyclerView);

        // Get the recipe title text passed
        if (getIntent().hasExtra("title_text")) {
            String titleText = getIntent().getStringExtra("title_text");
            TextView titleTextTextView = (TextView) findViewById(R.id.recipeTitleText);
            titleTextTextView.setText(titleText);

        } else {
            Toast.makeText(getApplicationContext(), "Recipe did not load properly", Toast.LENGTH_SHORT).show();
        }

        // set the initial value of recipe id to -1
        recipeId = -1;

        // Get the recipe id passed, if it exists
        if (getIntent().hasExtra("recipe_id")) {
            recipeId = getIntent().getIntExtra("recipe_id", -1);


            // Get information from the database name, image, description, link, prep_time
            DatabaseHelperRecipes db = new DatabaseHelperRecipes(getApplicationContext());
            Cursor cursor = db.getRecipeFromId(recipeId);

            if (cursor.getCount() > 0){
                cursor.moveToNext();

                // set class variables
                String name = cursor.getString(0);
                byte[] image = cursor.getBlob(1);
                String description = cursor.getString(2);
                String link = cursor.getString(3);
                Float prepTime = cursor.getFloat(4);

                // 1. Extract basic recipe data
                TextView recipeNameEditText = (TextView) findViewById(R.id.recipeName_edittext);
                recipeNameEditText.setText(name);

                ImageView recipeImageView = (ImageView) findViewById(R.id.recipeImage);
                if (image.length != 0){
                    Bitmap bm = BitmapFactory.decodeByteArray(image, 0, image.length);
                    recipeImageView.setImageBitmap(bm);
                }

                TextView recipeDescEditText = (TextView) findViewById(R.id.recipeDesc_edittext);
                recipeDescEditText.setText(description);

                TextView recipePrepTimeEditText = (TextView) findViewById(R.id.recipePrepTime_edittext);
                recipePrepTimeEditText.setText(String.valueOf(prepTime));

                TextView recipeLinkEditText = (TextView) findViewById(R.id.recipeLink_edittext);
                recipeLinkEditText.setText(link);


                // 2. Extract tags data (taglist and ingredientlist are reset initially)
                Cursor cursorTags = db.getTagsFromId(recipeId);
                if (cursorTags.getCount() > 0){
                    while (cursorTags.moveToNext()){
                        tagList.add(cursorTags.getString(0));
                    }
                }
                // modify the recyclerview of tags that are displayed
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
                tagsRecyclerView.setLayoutManager(gridLayoutManager);
                tagsRecyclerView.setAdapter(new StringAdapter(this, tagList));


                // 3. Extract ingredients data
                Cursor cursorIngredients = db.getIngredientsFromId(recipeId);
                if (cursorIngredients.getCount() > 0){
                    while (cursorIngredients.moveToNext()){
                        String ingredientName = cursorIngredients.getString(0);
                        Float ingredientAmount = cursorIngredients.getFloat(1);
                        String ingredientSupermarket = cursorIngredients.getString(2);
                        Float ingredientCost = cursorIngredients.getFloat(3);
                        int ingredientShelfLife = cursorIngredients.getInt(4);

                        Ingredient ingredient = new Ingredient(ingredientName, ingredientAmount, ingredientSupermarket, ingredientCost, ingredientShelfLife);
                        ingredientList.add(ingredient);
                    }
                }

                // modify the recycler view that displays list of items
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                ingredientsRecyclerView.setLayoutManager(linearLayoutManager);

                IngredientAdapter ingredientRecyclerViewAdapter = new IngredientAdapter(this, ingredientList);
                ingredientsRecyclerView.setAdapter(ingredientRecyclerViewAdapter);


                // 4. Extract cost data
                Cursor cursorCost = db.getWeightedCost(recipeId);
                if (cursorCost.getCount() > 0){
                    cursorCost.moveToNext();
                    float weightedCost = cursorCost.getFloat(0);

                    TextView totalCostTextView = (TextView) findViewById(R.id.totalCost_textView);
                    String totalCostString = "Total weighted cost: " + String.format(Locale.US, "%.2f", weightedCost);
                    totalCostTextView.setText(totalCostString);
                }

                db.close();

            } else {
                Toast.makeText(getApplicationContext(), "Recipe not found", Toast.LENGTH_SHORT).show();
            }
        }





        // load the linear layouts
        ingredientSuggestionLinearLayout = findViewById(R.id.ingredientSuggestion_linearLayout);
        ingredientFieldsLinearLayout = findViewById(R.id.ingredientFields_linearLayout);

        // initially, it is not visible
        ingredientSuggestionLinearLayout.setVisibility(View.GONE);

        // load the ingredient suggestion adapter here: No need dynamic update -> Not added to db yet
        ingredientSuggestionRecyclerView = findViewById(R.id.ingredientSuggestion_recyclerView);
        ArrayList<IngredientSuggestion> ingredientSuggestionList = displayIngredientSuggestionsFromDatabase();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        ingredientSuggestionRecyclerView.setLayoutManager(linearLayoutManager);
        ingredientSuggestionRecyclerViewAdapter = new IngredientSuggestionRecyclerViewAdapter(this, ingredientSuggestionList, ingredientFieldsLinearLayout);
        ingredientSuggestionRecyclerView.setAdapter(ingredientSuggestionRecyclerViewAdapter);

        // set up the ingredient edit text
        ingredientEditText = (TextView) findViewById(R.id.recipeIngredient_edittext);
        ingredientEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // empty charsequence -> Set linear layout invisible
                if (charSequence.length() == 0){
                    ingredientSuggestionLinearLayout.setVisibility(View.GONE);

                } else {

                    // get filter and filter it by the input string
                    ingredientSuggestionRecyclerViewAdapter.getFilter().filter(charSequence, new Filter.FilterListener() {
                        @Override
                        public void onFilterComplete(int i) {
                            // if there are no results, then set the recyclerview to be invisible
                            if (i == 0){
                                ingredientSuggestionLinearLayout.setVisibility(View.GONE);
                            } else {
                                ingredientSuggestionLinearLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // load other edit texts
        amountEditText = (TextView) findViewById(R.id.recipeAmount_edittext);
        supermarketEditText = findViewById(R.id.recipeSupermarket_autoCompleteTextView);
        costEditText = (TextView) findViewById(R.id.recipeCost_edittext);
        shelfLifeEditText = (TextView) findViewById(R.id.recipeShelfLife_edittext);


        // set up exit button
        ImageButton exitButton = findViewById(R.id.exit_imageButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitActivity();
            }
        });


        // set up the image button to redirect to link
        redirectImageButton = findViewById(R.id.redirect_imageButton);
        redirectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the url
                String url = ((TextView) findViewById(R.id.recipeLink_edittext)).getText().toString();
                if (!url.isEmpty()){
                    // append the start if it doesnt start with it
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }

                    // open url
                    Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(urlIntent);
                } else {
                    Toast.makeText(AddNewRecipe.this, "Recipe link is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // load all supermarket names from the db to the set
        currentSupermarketSet = new HashSet<>();

        DatabaseHelperRecipes db = new DatabaseHelperRecipes(getApplicationContext());
        Cursor cursor = db.getDistinctSupermarkets();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                currentSupermarketSet.add(cursor.getString(0));
            }
        }
        db.close();

        setUpSupermarketAutoCompleteTextView();

    }


    // function to exit activity
    public void exitActivity(){
        // set up dialog
        Dialog dialog = new Dialog(AddNewRecipe.this);
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

    // override the on back pressed function
    @Override
    public void onBackPressed() {
        exitActivity();
    }

    // method to set up supermarket auto complete text view. called at start or when new ing is added.
    public void setUpSupermarketAutoCompleteTextView(){

        ArrayList<String> currentSupermarketSetStrings = new ArrayList<>(currentSupermarketSet);
        StartsWithFilterArrayAdapter arrayAdapterSupermarket = new StartsWithFilterArrayAdapter(AddNewRecipe.this, R.layout.recipe_dropdown_item, currentSupermarketSetStrings);
        supermarketEditText.setAdapter(arrayAdapterSupermarket);

        supermarketEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    supermarketEditText.showDropDown();
                }
            }
        });

        supermarketEditText.setThreshold(1);

        supermarketEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // filter here if text changed
                arrayAdapterSupermarket.getFilter().filter(charSequence);
                supermarketEditText.showDropDown();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
        // reset fields
        ingredientEditText.setText("");
        amountEditText.setText("");
        supermarketEditText.setText("");
        costEditText.setText("");
        shelfLifeEditText.setText("");
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
        String ingredient = ingredientEditText.getText().toString().trim();
        String amountStr = amountEditText.getText().toString();
        String supermarket = supermarketEditText.getText().toString().trim();
        String costStr = costEditText.getText().toString();
        String shelfLifeStr = shelfLifeEditText.getText().toString();

        // empty ingredient name submitted
        if (ingredient.isEmpty()){
            Toast.makeText(AddNewRecipe.this, "Ingredient name is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // empty portion size and type cast
        float amount = 0.0f;
        if (!amountStr.isEmpty()){
            amount = Float.parseFloat(amountStr);
        }

        // empty supermarket --> nullable

        // empty cost
        float cost = 0.0f;
        if (!costStr.isEmpty()){
            cost = Float.parseFloat(costStr);
            cost = Math.round(cost * 100) / 100.0f;  // trim to 2 dp
        }

        // empty shelf life
        int shelfLife = 365;
        if (!shelfLifeStr.isEmpty()){
            shelfLife = Integer.parseInt(shelfLifeStr);
        }

        // construct ingredient and add to arraylist
        Ingredient newIngredient = new Ingredient(ingredient, amount, supermarket, cost, shelfLife);
        ingredientList.add(newIngredient);

        // modify the recycler view that displays list of items
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        ingredientsRecyclerView.setLayoutManager(linearLayoutManager);

        IngredientAdapter ingredientRecyclerViewAdapter = new IngredientAdapter(this, ingredientList);
        ingredientsRecyclerView.setAdapter(ingredientRecyclerViewAdapter);

        // add supermarket to current hashset if not null
        if (!supermarket.isEmpty() && !currentSupermarketSet.contains(supermarket)){
            currentSupermarketSet.add(supermarket);
            setUpSupermarketAutoCompleteTextView();
        }

        // reset fields except supermarket
        ingredientEditText.setText("");
        amountEditText.setText("");
        costEditText.setText("");
        shelfLifeEditText.setText("");

        updateCost();
    }

    // method to update the cost displayed according to items
    public void updateCost(){

        TextView totalCost = (TextView) findViewById(R.id.totalCost_textView);

        // If no ingredients
        if (ingredientList.isEmpty()){
            String totalCostString = getResources().getString(R.string.total_weighted_cost_default);
            totalCost.setText(totalCostString);
        }

        else{  // Else calculate sum
            float total = 0.0f;
            for (Ingredient i : ingredientList){
                total += i.getCost()*i.getAmount();
            }
            String totalCostString = "Total weighted cost: " + String.format(Locale.US, "%.2f", total);
            totalCost.setText(totalCostString);
        }
    }


    // method when the save button is clicked: Add data to database
    public void updateRecipeToDatabase(View v){

        String name = ((TextView) findViewById(R.id.recipeName_edittext)).getText().toString();
        // name cannot be empty
        if (name.isEmpty()){
            Toast.makeText(AddNewRecipe.this, "Recipe name is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        ImageView recipeImage = (ImageView) findViewById(R.id.recipeImage);
        // convert image to byte array to be stored as blob in the db
        byte[] recipeImageByteArray = {};
        BitmapDrawable recipeImageDrawable = (BitmapDrawable) recipeImage.getDrawable();

        if (recipeImageDrawable == null){
            recipeImageDrawable = (BitmapDrawable) getDrawable(R.drawable.question_mark);
        }

        // this is just a safe condition check, it must pass
        if (recipeImageDrawable != null){
            Bitmap bitmap = recipeImageDrawable.getBitmap();
            int originalWidth = bitmap.getWidth();
            int originalHeight = bitmap.getHeight();

            int newHeight = 0;
            int newWidth = 0;

            // compression algorithm --> Retain its dimensions
            if (originalHeight >= originalWidth) {
                newHeight = 64;
                newWidth = Math.round((float) (originalWidth * newHeight) / originalHeight);
            } else {
                newWidth = 64;
                newHeight = Math.round((float) (originalHeight * newWidth) / originalWidth);
            }

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            recipeImageByteArray = byteArrayOutputStream.toByteArray();

        }

        String description = ((TextView) findViewById(R.id.recipeDesc_edittext)).getText().toString();
        String link = ((TextView) findViewById(R.id.recipeLink_edittext)).getText().toString();
        String prepTimeString = ((TextView) findViewById(R.id.recipePrepTime_edittext)).getText().toString();
        // if prep time is null, set it to 0.0f
        float prepTime = 0.0f;
        if (!prepTimeString.isEmpty()){
            prepTime = Float.parseFloat(prepTimeString);
        }


        // tags and ingredients are stored in tagList and ingredientList which is passed in HERE
        DatabaseHelperRecipes db = new DatabaseHelperRecipes(AddNewRecipe.this);

        // Create recipe or update recipe depending on whether recipeId is -1
        if (recipeId == -1) {
            // Create recipe
            boolean status  = db.addRecipe(name, recipeImageByteArray, description, link, prepTime, tagList, ingredientList);

            // exit activity if successful
            // add to database failed
            if (!status){
                Toast.makeText(this, "Data adding failed", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "New recipe added", Toast.LENGTH_SHORT).show();
                getOnBackPressedDispatcher().onBackPressed();
            }

        } else {
            // update recipe from recipeId
            boolean status = db.updateRecipeFromId(recipeId, name, recipeImageByteArray, description, link, prepTime);
            status = status && db.updateRecipeTagsFromId(recipeId, tagList);
            status = status && db.updateRecipeIngredientsFromId(recipeId, ingredientList);

            if (!status){
                Toast.makeText(this, "Data adding failed", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Recipe saved", Toast.LENGTH_SHORT).show();
                getOnBackPressedDispatcher().onBackPressed();
            }

        }

        db.close();
    }

    // used for the adapter.
    public ArrayList<IngredientSuggestion> displayIngredientSuggestionsFromDatabase(){
        ArrayList<IngredientSuggestion> ingredientSuggestionList = new ArrayList<>();

        DatabaseHelperRecipes db = new DatabaseHelperRecipes(getApplicationContext());

        Cursor cursor = db.getIngredients();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String name = cursor.getString(0);
                String supermarket = cursor.getString(1);
                float cost = cursor.getFloat(2);
                int shelfLife = cursor.getInt(3);

                IngredientSuggestion ingredientSuggestion = new IngredientSuggestion(name, supermarket, cost, shelfLife);
                ingredientSuggestionList.add(ingredientSuggestion);

            }
        }

        db.close();

        return ingredientSuggestionList;
    }
}