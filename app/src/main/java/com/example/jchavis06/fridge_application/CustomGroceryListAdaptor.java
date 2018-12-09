package com.example.jchavis06.fridge_application;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomGroceryListAdaptor extends BaseAdapter {

    private Context context; //context
    private ArrayList<GroceryListItem> items; //data source of the list adapter

    //public constructor
    public CustomGroceryListAdaptor(Context context, ArrayList<GroceryListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.shopping_list_row, parent, false);
        }

        // get current item to be displayed
        final GroceryListItem currentItem = (GroceryListItem) getItem(position);

        // get the TextView for item name and item description
        TextView textViewItemName = (TextView)
                convertView.findViewById(R.id.itemNameView);
        TextView textViewItemQuantity = (TextView)
                convertView.findViewById(R.id.itemQuantityView);
        TextView textViewItemQuantityType = (TextView)
                convertView.findViewById(R.id.itemQuantityTypeView);
        TextView textViewItemBrand = (TextView)
                convertView.findViewById(R.id.itemBrandView);
        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.deleteButton);

        //sets the text for item name and item description from the current item object
        textViewItemName.setText(currentItem.getItemName());
        textViewItemQuantity.setText("" + currentItem.getQuantity());

        textViewItemQuantityType.setText(currentItem.getQuantityType());
        textViewItemBrand.setText("" + currentItem.getBrand());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("Add Item")
                        .setMessage("Are you sure you want to delete '" + currentItem.getItemName() + "'?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                GroceryListWriter glw = new GroceryListWriter(context);
                                glw.removeFromGroceryList(currentItem.getItemName());
                                items.remove(position);
                                notifyDataSetChanged();

                                Toast toast = Toast.makeText(context,
                                        "Successfully deleted '" + currentItem.getItemName() + "'",
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        // returns the view for the current row
        return convertView;
    }
}
