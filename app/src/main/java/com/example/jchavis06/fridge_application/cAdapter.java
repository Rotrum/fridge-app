package com.example.jchavis06.fridge_application;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class cAdapter extends ArrayAdapter {
    //to reference the Activity
    private final Activity context;
    //to store the list of countries
    protected String[] nameArray;
    //to store the list of countries
    protected String[] msgArray;

    public cAdapter(Activity contex, String[] nameArr, String[] msgArr){

        super(contex,R.layout.ms_row , nameArr);
        context=contex;
        nameArray = nameArr;
        msgArray = msgArr;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.ms_row, null,true);

        //this code gets references to objects in the xml file
        TextView nameTextField = rowView.findViewById(R.id.msg_nam);
        TextView msgTextField = rowView.findViewById(R.id.msg);

        //this code sets the values of the objects to values from the arrays
        String nameText = "\t-" + nameArray[position];
        nameTextField.setText(nameText);
        msgTextField.setText(msgArray[position]);

        return rowView;

    };

    /*
    public void update(String[] msgs, String[] names)
    {
        nameArray = names;
        msgArray = msgs;
        notifyDataSetChanged();
    }
    */
}

