package com.example.recipeapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment {

    // Store list of shopping list preview lists
    ArrayList<ShoppingListPreview> shoppingListPreviewArrayList;
    private RecyclerView shoppingListRecyclerView;
    private ShoppingListAdapter shoppingListAdapter;  // The adapter for displaying a list of recipe previews


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ShoppingListFragment newInstance(String param1, String param2) {
        ShoppingListFragment fragment = new ShoppingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /*
    Executed once when the fragment is created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        // load SL Recycler view
        shoppingListRecyclerView = (RecyclerView) view.findViewById(R.id.shoppingList_recyclerView);

        return view;
    }

    /*
    Executed every time when the fragment is reloaded
    such as updating recycler view
     */
    @Override
    public void onResume() {
        // Updates the recipe displayed whenever the page is loaded
        super.onResume();

        View view = getView();

        // 1. Updates the recipe count displayed
        int count = displayShoppingListCountFromDatabase();
        String countString = String.valueOf(count) + " results";
        TextView textView = (TextView) view.findViewById(R.id.shoppingListCount_textView);
        textView.setText(countString);

        // If there are no SL, display empty SL message
        TextView emptyShoppingListTextView = view.findViewById(R.id.emptyShoppingList_textView);
        if (count > 0){
            emptyShoppingListTextView.setVisibility(View.GONE);
        } else {
            emptyShoppingListTextView.setVisibility(View.VISIBLE);
        }


        // 2. Update the SL recyclerview: load SL Preview arraylist from db
        shoppingListPreviewArrayList = displayShoppingListsFromDatabase();
        // set up recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        shoppingListRecyclerView.setLayoutManager(linearLayoutManager);

        // crete a new adapter from the modified SL preview array list
        shoppingListAdapter = new ShoppingListAdapter(getActivity(), shoppingListPreviewArrayList);
        shoppingListRecyclerView.setAdapter(shoppingListAdapter);
    }


    public int displayShoppingListCountFromDatabase(){
        DatabaseHelperShoppingLists db = new DatabaseHelperShoppingLists(getActivity().getApplicationContext());
        Cursor cursor = db.getShoppingListsCount();

        // there is guaranteed to have one data
        if (cursor.getCount() > 0){
            cursor.moveToNext();
            return cursor.getInt(0);
        } else {
            return 0;
        }
    }


    public ArrayList<ShoppingListPreview> displayShoppingListsFromDatabase(){
        ArrayList<ShoppingListPreview> shoppingListPreviewArrayList = new ArrayList<>();

        // extract all SL PREVIEW data from the database
        DatabaseHelperShoppingLists db = new DatabaseHelperShoppingLists(getActivity().getApplicationContext());
        Cursor cursor = db.getShoppingLists();

        // if have data
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int shoppingListId = cursor.getInt(0);
                String name = cursor.getString(1);
                boolean isFavourited = (cursor.getInt(2) == 1);

                // get the other data if there is count
                Cursor cursor2 = db.getShoppingListsNumsFromId(shoppingListId);
                cursor2.moveToNext();
                int itemCount = cursor2.getInt(0);
                int supermarketCount = cursor2.getInt(1);
                float cost = cursor2.getFloat(2);

                // construct preview object
                ShoppingListPreview shoppingListPreview = new ShoppingListPreview(name, itemCount, supermarketCount, cost, isFavourited);

                shoppingListPreviewArrayList.add(shoppingListPreview);
            }
        }

        return shoppingListPreviewArrayList;
    }
}