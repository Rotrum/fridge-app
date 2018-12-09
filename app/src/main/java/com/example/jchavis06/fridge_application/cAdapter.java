package com.example.jchavis06.fridge_application;

import android.app.Activity;
import android.content.Context;
import android.opengl.Visibility;
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
import java.util.Arrays;

public class cAdapter extends ArrayAdapter {
    //to reference the Activity
    private final Activity context;
    //to store the list of senders
    protected ArrayList<String> nameArray;
    //to store the list of msgs
    protected ArrayList<String> msgArray;
    //to store the list of recipients
    protected ArrayList<String> recArray;
    //Hidden?
    protected ArrayList<Boolean> hidArray;
    //Edited?
    protected ArrayList<Boolean> edArray;

    public cAdapter(Activity contex, ArrayList<String> nameArr,
                    ArrayList<String> msgArr, ArrayList<String> recArr) {

        super(contex, R.layout.ms_row, nameArr);
        context = contex;
        nameArray = new ArrayList<String>();
        msgArray = new ArrayList<String>();
        recArray = new ArrayList<String>();
        hidArray = new ArrayList<Boolean>();
        edArray = new ArrayList<Boolean>();
        nameArray.addAll(nameArr);
        msgArray.addAll(msgArr);
        recArray.addAll(recArr);
        for (int i = 0; i < msgArray.size(); i++) {
            hidArray.add(false);
            edArray.add(false);
        }
        edArray.set(9, true);
    }

    public cAdapter(Activity contex, ArrayList<String> nameArr,
                    ArrayList<String> msgArr, ArrayList<String> recArr,
                    ArrayList<Boolean> hidArr, ArrayList<Boolean> edArr) {

        super(contex, R.layout.ms_row, nameArr);
        context = contex;
        nameArray = new ArrayList<String>();
        msgArray = new ArrayList<String>();
        recArray = new ArrayList<String>();
        hidArray = new ArrayList<Boolean>();
        edArray = new ArrayList<>();
        nameArray.addAll(nameArr);
        msgArray.addAll(msgArr);
        recArray.addAll(recArr);
        hidArray.addAll(hidArr);
        edArray.addAll(edArr);
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View row = view;

        if (row == null) {
            row = inflater.inflate(R.layout.ms_row, null, true);
        }
        //this code gets references to objects in the xml file
        TextView nameTextField = row.findViewById(R.id.msg_nam);
        TextView msgTextField = row.findViewById(R.id.msg);
        TextView edTex = row.findViewById(R.id.msg_ed);

        //this code sets the values of the objects to values from the arrays
        String nameText = "\t-" + nameArray.get(position);
        String recTex = recArray.get(position);
        if (!recTex.equals("Fridge"))
            nameText = nameText + " (To " + recTex + ")";
        nameTextField.setText(nameText);
        msgTextField.setText(msgArray.get(position));
        msgTextField.setTextSize(16);
        nameTextField.setVisibility(View.VISIBLE);
        if (edArray.get(position) == true)
            edTex.setVisibility(View.VISIBLE);
        if (hidArray.get(position) == true) {
            msgTextField.setText("Hidden");
            msgTextField.setTextSize(14);
            nameTextField.setVisibility(View.GONE);
            edTex.setVisibility(View.GONE);
        }

        return row;

    }

    ;

    public void hide(int p) {
        hidArray.set(p, !hidArray.get(p));
        notifyDataSetChanged();
    }

    public void add(String name, String msg, String rec) {
        nameArray.add(name);
        msgArray.add(msg);
        recArray.add(rec);
        hidArray.add(false);
        edArray.add(false);
    }

    public void delete(int p) {
        nameArray.remove(p);
        msgArray.remove(p);
        recArray.remove(p);
        hidArray.remove(p);
        edArray.remove(p);
        notifyDataSetChanged();
    }

    public void edit(int p, String s) {
        msgArray.set(p, s);
        edArray.set(p, true);
        notifyDataSetChanged();
    }

    public boolean isHidden(int p) {
        return hidArray.get(p);
    }

    public int getCount() {
        return msgArray.size();
    }

    public String getSender(int p) {
        return nameArray.get(p);
    }

    public String getMsg(int p) {
        return msgArray.get(p);
    }


    public ArrayList<String> getMsgs() {
        return msgArray;

    }

    public ArrayList<String> getNames() {
        return nameArray;
    }

    public ArrayList<String> getRecip() {
        return recArray;
    }

    public ArrayList<Boolean> getHid() {
        return hidArray;
    }

    public ArrayList<Boolean> getEd() {
        return edArray;
    }
}
