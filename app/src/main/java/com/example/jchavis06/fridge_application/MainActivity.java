package com.example.jchavis06.fridge_application;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    protected LayoutInflater infl;
    protected FrameLayout con1;
    protected FrameLayout con2;
    protected cAdapter msgr;
    protected static boolean reset_default = false;
    protected Context context;

    private ListView mShoppingList;
    private EditText mItemEdit;
    private Button mAddButton;
    protected String curItem;
    protected GroceryListWriter glw = null;
    protected GroceryListWriter glw1 = null;
    protected EditText editText;
    private ArrayAdapter<String> mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setHide();
        context = getApplicationContext();
        con1 = findViewById(R.id.content1);
        con2 = findViewById(R.id.content2);
        infl = getLayoutInflater();
        infl.inflate(R.layout.msg_pg, con1, true);
        infl.inflate(R.layout.shopping_list, con2, true);
        setFile();
        setShop();
        setButwr();
        setMsg();
    }

    protected void setShop()
    {
        Log.d("TAG", "onCreate()");

        mShoppingList =  findViewById(R.id.shopping_listView);
        mItemEdit =  findViewById(R.id.item_editText);
        mAddButton = findViewById(R.id.add_button);
        setEdit2();
       // if(glw1 == null)
          glw1 = new GroceryListWriter(context);
        Log.e("Tag", "About to read from the grocery list file.");
        final ArrayList<String> groceryList = glw1.readGroceryList();

        for (String item : groceryList) {
            mAdapter.add(item);
        }
        setButad();

        //mAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        //mShoppingList.setAdapter(mAdapter);

        mShoppingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                curItem = (String) ((TextView) view).getText();
                con2.removeAllViews();
                infl.inflate(R.layout.grocery_item, con2, true);
                setGtem();
            }
        });
    }

    protected void setGtem()
    {
        Button saveButton;
        Button removeButton;

        TextView tv1 = findViewById(R.id.textView);
        tv1.setText("Edit " + curItem);

        saveButton = findViewById(R.id.save_button);
        removeButton =  findViewById(R.id.remove_item_button);
        editText = findViewById(R.id.editText);
        if(glw == null)
            glw = new GroceryListWriter(context);
        String description = glw.readGroceryListItemDescription(curItem);
        editText.setText(description);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this is where we save the file and go back to the grocery list view.
                String desc = editText.getText().toString();
                Log.d("DESCRIPTION", "Description: " + desc);
                glw.editGroceryListItemDescription(curItem, desc);
                con2.removeAllViews();
                infl.inflate(R.layout.shopping_list, con2, true);
                mAdapter.clear();
                setShop();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this is where we remove the item from the grocery list
                glw.removeFromGroceryList(curItem);
                con2.removeAllViews();
                infl.inflate(R.layout.shopping_list, con2, true);
                mAdapter.clear();
                setShop();
            }
        });
    }


    protected void setButad()
    {
        if(mAdapter == null)
             mAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        mShoppingList.setAdapter(mAdapter);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = mItemEdit.getText().toString();
                Log.e("ADDING", "Trying to add: " + item + " to the grocery list");
                GroceryListWriter glw2 = new GroceryListWriter(context);
                ArrayList<String> current_list = glw2.readGroceryList();

                if (current_list.contains(item)) {
                    //print error message
                    Log.e("TAG", "Grocery list already contains this item.");


                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(context);
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
                }//end else

            }//end onclick

        });//end listener

    }

    protected void setHide()
    {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    protected void setFile()
    {
        //Context = getApplicationContext();
        String DestinationFile = context.getFilesDir().getPath() + File.separator + "defmsg.txt";
        File dest = new File(DestinationFile);

        String DFile = context.getFilesDir().getPath() + File.separator + "groceryList.txt";
        File dest2 = new File(DFile);
        if(!reset_default && dest.exists())
        {
            dest.delete();
        }
        if(dest2.exists())
        {
            dest2.delete();
        }
        if (!dest.exists()) {
            try {
                copyToStorage(context, "defmsg.txt", DestinationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }//end try-catch
        }//end if

        reset_default = true;
    }

    protected void setButwr()
    {
        Button wr = findViewById(R.id.butwr);
        wr.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                con1.removeAllViews();
                infl.inflate(R.layout.msg_lv, con1, true);
                setButpst();
            }
        });
    }

    protected void setButpst()
    {
        Button post = findViewById(R.id.msg_post);
        post.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                EditText e = findViewById(R.id.msg_input);
                String s = e.getText() + "\nYou\n";
                writ(getFilesDir().getPath() + File.separator, "defmsg.txt", s);
                con1.removeAllViews();
                infl.inflate(R.layout.msg_pg, con1, true);
                setButwr();
                setMsg();
            }
        });
         setEdit();
    }

    protected void setMsg()
    {
        String[] s = readFile();

        int sz = s.length;
        String[] msgArray = Arrays.copyOfRange(s, 0, sz/2);
        String[] nameArray = Arrays.copyOfRange(s, sz/2, sz);

        msgr = new cAdapter(this, nameArray, msgArray);

        ListView listView = findViewById(R.id.msg_scr);
        listView.setAdapter(msgr);

    }

    protected String[] readFile()
    {
        FileInputStream is = null;
        //Context context = getApplicationContext();
        String s;
        ArrayList<String> msgs = new ArrayList<String>();
        ArrayList<String> names = new ArrayList<String>();
        try {
            is = context.openFileInput("defmsg.txt");
            BufferedReader sc = new BufferedReader(new InputStreamReader(is));

            while((s = sc.readLine()) != null)
            {
                msgs.add(s);
                names.add(sc.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int sz = msgs.size();
        String[] msgbrd = new String[sz*2];
        for(int i = 0; i < sz; i++)
            msgbrd[i] = msgs.get(i);
        for(int i = sz; i < sz*2; i++)
            msgbrd[i] = names.get(i - sz);
        if(is != null)
        {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }//end try-catch
        }//end if
        return msgbrd;
    }

    public void writ(String dir, String file, String msg){
        File out;
        OutputStreamWriter outsw = null;
        FileOutputStream fouts = null;

        out = new File(new File(dir), file);

        if ( out.exists() == false ){
            try {
                out.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }//end t-catch
        }//end if

        try {
            fouts = new FileOutputStream(out, true);
            outsw = new OutputStreamWriter(fouts);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            outsw.append(msg);
            outsw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void setEdit()
    {
        EditText e = findViewById(R.id.msg_input);
        e.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                for(int i = s.length()-1; i >= 0; i--){
                    if(s.charAt(i) == '\n'){
                        s.delete(i, i + 1);
                        return;
                    }
                }
            }
        });
    }

    protected void setEdit2()
    {
        EditText e = findViewById(R.id.item_editText);
        e.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                for(int i = s.length()-1; i >= 0; i--){
                    if(s.charAt(i) == '\n'){
                        s.delete(i, i + 1);
                        return;
                    }
                }
            }
        });
    }

    private void copyToStorage(Context Context, String SourceFile, String DestinationFile) throws IOException {
        InputStream is = Context.getAssets().open(SourceFile);
        OutputStream os = new FileOutputStream(DestinationFile);
        copy(is, os);
        os.flush();
        os.close();
        is.close();
    }


    private void copy(InputStream Input, OutputStream Output) throws IOException
    {
        byte[] b = new byte[5120];
        int length = Input.read(b);
        while (length > 0) {
            Output.write(b, 0, length);
            length = Input.read(b);
        }
    }



}

