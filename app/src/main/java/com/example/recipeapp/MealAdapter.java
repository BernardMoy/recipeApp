package com.example.recipeapp;

import static androidx.core.content.ContextCompat.getDrawable;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MealAdapter extends RecyclerView.Adapter<MealRecyclerViewHolder> {

    // an arraylist to store a list of meal preview objects.
    private Context ctx;
    private ArrayList<MealPreview> mealPreviewList;

    // two buttons for dialog
    private Button cancelButton;
    private Button deleteButton;

    public MealAdapter(Context ctx, ArrayList<MealPreview> mealPreviewList){
        this.ctx = ctx;
        this.mealPreviewList = mealPreviewList;
    }

    @NonNull
    @Override
    public MealRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MealRecyclerViewHolder(LayoutInflater.from(ctx).inflate(R.layout.meal_row, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MealRecyclerViewHolder holder, int position) {
        MealPreview mealPreview = mealPreviewList.get(position);

        // set text for the meal rows
        holder.getCategoryName().setText(mealPreview.getCategory());

        byte[] imageByteArray = mealPreview.getRecipeImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        holder.getRecipeImage().setImageBitmap(bitmap);

        holder.getRecipeName().setText(mealPreview.getRecipeName());

        holder.getRecipeCost().setText(String.valueOf(mealPreview.getRecipeCost()));

        // set delete button functionality
        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set up dialog
                Dialog dialog = new Dialog(ctx);
                dialog.setContentView(R.layout.confirm_window);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(getDrawable(ctx, R.drawable.custom_edit_text));
                dialog.setCancelable(false);

                // load the two buttons
                cancelButton = dialog.findViewById(R.id.confirmRecipeCancel_button);
                deleteButton = dialog.findViewById(R.id.confirmRecipeDelete_button);

                // Set on click listeners for the two buttons
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Remove the dialog
                        dialog.dismiss();
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // get the meal id that is to be deleted
                        int pos = holder.getAdapterPosition();
                        int mealId = mealPreviewList.get(pos).getMealId();

                        // delete Recipe from arraylists
                        mealPreviewList.remove(pos);
                        notifyItemRemoved(pos);

                        // display empty message if all recipes are deleted
                        if (mealPreviewList.isEmpty()){
                            TextView emptyRecipeTextView = (TextView) ((Activity) ctx).findViewById(R.id.emptyMeal_textView);
                            emptyRecipeTextView.setVisibility(View.VISIBLE);
                        }

                        // delete meal from db
                        DatabaseHelperRecipes db = new DatabaseHelperRecipes(ctx);
                        db.deleteMealFromId(mealId);
                        db.close();

                        // dismiss dialog
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealPreviewList.size();
    }
}
