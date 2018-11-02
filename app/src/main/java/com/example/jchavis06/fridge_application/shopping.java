package com.example.jchavis06.fridge_application;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class shopping extends AppCompatActivity {

        private ListView mShoppingList;
        private EditText mItemEdit;
        private Button mAddButton;

        private ArrayAdapter<String> mAdapter;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d("TAG", "onCreate()");
            //Button rd = findViewById(R.id.butrd)
            mShoppingList = (ListView) findViewById(R.id.shopping_listView);
            mItemEdit = (EditText) findViewById(R.id.item_editText);
            mAddButton = (Button) findViewById(R.id.add_button);


            mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            mShoppingList.setAdapter(mAdapter);

            GroceryListWriter glw = new GroceryListWriter(getApplicationContext());
            Log.e("Tag", "About to read from the grocery list file.");
            final ArrayList<String> groceryList = glw.readGroceryList();

            for (String item : groceryList) {
                mAdapter.add(item);
            }

            mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String item = mItemEdit.getText().toString();
                    Log.e("ADDING", "Trying to add: " + item + " to the grocery list");
                    GroceryListWriter glw2 = new GroceryListWriter(shopping.this);
                    ArrayList<String> current_list = glw2.readGroceryList();
                    if (current_list.contains(item)) {
                        //print error message
                        Log.e("TAG", "Grocery list already contains this item.");


                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(shopping.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(shopping.this);
                        }
                        builder.setTitle("Add Item")
                                .setMessage("This item is already in the grocery list.")
                                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    } else {
                        mAdapter.add(item);
                        glw2.addToGroceryList(item, "");
                        mAdapter.notifyDataSetChanged();
                        mItemEdit.setText("");
                    }
                }
            });

            mShoppingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String grocery_item = (String) ((TextView) view).getText();
                    Intent newIntent = new Intent(shopping.this, grocery_item.class);
                    newIntent.putExtra("item", grocery_item);
                    startActivity(newIntent);
                }
            });


        }



}
