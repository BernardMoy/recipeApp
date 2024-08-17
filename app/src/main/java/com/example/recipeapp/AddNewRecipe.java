package com.example.recipeapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

public class AddNewRecipe extends AppCompatActivity {

    // Result launcher for the image picker
    ActivityResultLauncher<Intent> resultLauncher;

    // stores current tags that are added
    private ArrayList<String> tags;

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
        tags = new ArrayList<>();
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

        tags.add(newTag);

        // clear box
        textview.setText("");
    }
}