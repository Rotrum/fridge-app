package com.example.jchavis06.fridge_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;

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
    protected Activity contex;
    private ListView listView;

    private ArrayList<String> nSen;
    private ArrayList<String> Sen;
    private TextView msgTxt;

    private ListView mShoppingList;
    private Button mAddButton;
    private ArrayAdapter<String> mAdapter;
    protected GroceryListWriter glw = null;
    protected GroceryListWriter glw1 = null;
    protected EditText editText;

    //Msg things don't ask
    protected static ArrayList<String> nArray;
    protected static ArrayList<String>  mArray;
    protected static ArrayList<String>  rArray;
    protected static ArrayList<Boolean> hArray;
    protected static ArrayList<Boolean> eArray;

    //add grocery item
    private EditText itemName;
    private EditText brandName;
    private EditText comments;
    private Spinner qty;
    private Spinner qtyLabel;
    private Button addItem;
    private boolean editing;
    private String grocery_item_old;
    private String curItem;

    private boolean canBack = true;
    private ArrayList<String> upd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        contex = MainActivity.this;
        con1 = findViewById(R.id.content1);
        con2 = findViewById(R.id.content2);
        infl = getLayoutInflater();
        infl.inflate(R.layout.msg_pg, con1, true);
        infl.inflate(R.layout.shopping_list, con2, true);
        setFile();
        setShop();
        setButwr();
        setMsg();
        setUpdate();
    }

    protected void setUpdate() {
        upd = new ArrayList<>();
        String[] s = {"Carrots", "Cucumbers", "Bananas", "Pizza", "Chicken Breasts"};
        upd.addAll(Arrays.asList(s));

        Button up = findViewById(R.id.update);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(upd.size() == 0)
                {
                    String ms = "No new items found";
                    Toast toast = Toast.makeText(context, ms, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 10);
                    ((TextView)((LinearLayout)toast.getView()).getChildAt(0))
                            .setGravity(Gravity.CENTER_HORIZONTAL);
                    toast.show();
                }
                else
                {
                    final String add = upd.get(0);
                    final String[] ppls = {"Everyone","Ana","Dad","Mom","Tim","Cancel"};
                    AlertDialog.Builder b2 = new AlertDialog.Builder(context);

                    b2.setTitle("Who does this belong to? \n" + "Item to be added: " + add);
                            b2.setItems(ppls, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    // the user clicked on nSen[i]
                                   if(i != 5) {
                                       String ms = "Successfully added " + add + " to ";
                                       if(i == 0)
                                           ms += "Shared Inventory";
                                       else
                                           ms += ppls[i] + "'s Inventory";
                                       Toast toast = Toast.makeText(context, ms, Toast.LENGTH_SHORT);
                                       toast.setGravity(Gravity.BOTTOM, 0, 50);
                                       ((TextView)((LinearLayout)toast.getView()).getChildAt(0))
                                               .setGravity(Gravity.CENTER_HORIZONTAL);
                                       toast.show();
                                       upd.remove(0);
                                   }
                                }
                            });
                            b2.show();
                }

            }
        });
    }



    protected void setShop()
    {
        canBack = true;
        mShoppingList = findViewById(R.id.shopping_listView);
        mAddButton = findViewById(R.id.add_item_button);

        GroceryListWriter glw = new GroceryListWriter(context);

        Log.e("Tag", "About to read from the grocery list file.");
        final ArrayList<String> groceryList = glw.readGroceryList();

        ArrayList<GroceryListItem> groceryListItems = new ArrayList<GroceryListItem>();

        for (String item: groceryList) {
            ArrayList<String> values = glw.readGroceryListItemValues(item);
            int quantity = Integer.parseInt(values.get(0));
            String quantityType = values.get(1);
            String brand = values.get(2);
            GroceryListItem list_item = new GroceryListItem(item, quantity, quantityType, brand);
            groceryListItems.add(list_item);
        }

        CustomGroceryListAdaptor adaptor = new CustomGroceryListAdaptor(this, groceryListItems);
        mShoppingList.setAdapter(adaptor);


        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                con2.removeAllViews();
                infl.inflate(R.layout.add_grocery_item, con2, true);
                curItem = null;
                setAdd();
            }
        });

        mShoppingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GroceryListItem selected = (GroceryListItem) adapterView.getAdapter().getItem(i);
                 curItem = selected.getItemName();
                con2.removeAllViews();
                infl.inflate(R.layout.add_grocery_item, con2, true);
                setAdd();
            }
        });
    }

    //Add grocery item
    protected void setAdd()
    {
       editing = false;
       canBack = false;
        Log.d("TAG", "onCreate()");
        String[] quantities = {"1","2","3","4","5", "6","7","8","9","10","11","12"};
        grocery_item_old = "";
        if(curItem != null)
           grocery_item_old = curItem;
        int old_quantity;
        String old_quantity_type;
        String brand;
        String comment;
        itemName = findViewById(R.id.itemName);
        brandName =  findViewById(R.id.brandText);
        comments =  findViewById(R.id.commentsText);
        qty = findViewById(R.id.quantitySpinner);
        qtyLabel =  findViewById(R.id.quantityTypeSpinner);
        addItem = findViewById(R.id.add_grocery_item_button);

        final String[] quantityTypes= {"","Bags","Boxes","Cartons","Gallons","lbs"};

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,quantities);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        qty.setAdapter(aa);

        ArrayAdapter aa2 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,quantityTypes);
        aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        qtyLabel.setAdapter(aa2);
        if (!grocery_item_old.equals("")) {
            GroceryListWriter gw = new GroceryListWriter(context);
            ArrayList<String> vals = gw.readGroceryListItemValues(grocery_item_old);
            //we are editing a grocery list item
            editing = true;
            addItem.setText("Done Editing");
            old_quantity = Integer.parseInt(vals.get(0));
            old_quantity_type = vals.get(1);
            brand = vals.get(2);
            comment = vals.get(3);

            itemName.setText(grocery_item_old);
            brandName.setText(brand);
            comments.setText(comment);

            ArrayAdapter myAdap = (ArrayAdapter) qty.getAdapter(); //cast to an ArrayAdapter

            int spinnerPosition = myAdap.getPosition("" + old_quantity);
            qty.setSelection(spinnerPosition);

            ArrayAdapter myAdap2 = (ArrayAdapter) qtyLabel.getAdapter(); //cast to an ArrayAdapter

            int spinnerPosition2 = myAdap2.getPosition("" + old_quantity_type);
            qtyLabel.setSelection(spinnerPosition2);

        }
        qty.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                       long id) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.colorPrimaryText));
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        qtyLabel.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                       long id) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.colorPrimaryText));
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all of the information and store it
                String grocery_item = itemName.getText().toString();
                int quantity = Integer.parseInt(qty.getSelectedItem().toString());
                String quantityType = qtyLabel.getSelectedItem().toString();
                String brand = brandName.getText().toString();
                String comment = comments.getText().toString();

                GroceryListWriter gw = new GroceryListWriter(getApplicationContext());

                ArrayList<String> currentList = gw.readGroceryList();
                if (editing == true) {
                    gw.editGroceryListItem(grocery_item_old, grocery_item, quantity, quantityType, brand, comment);
                } else if (grocery_item.equals("")) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }
                    builder.setTitle("Add Item")
                            .setMessage("Please Fill Out The Name Of The Item.")
                            .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                } else if (currentList.contains(grocery_item)) {
                    //already exists in the list. popup to tell them no.
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
                    return;
                } else {
                    gw.addToGroceryList(grocery_item, quantity, quantityType, brand, comment);
                }
                con2.removeAllViews();
                infl.inflate(R.layout.shopping_list, con2, true);
                setShop();
            }
        });
    }

    protected void setFile()
    {
        //Context = getApplicationContext();
        String DestinationFile = context.getFilesDir().getPath() + File.separator + "defmsg.txt";
        File dest = new File(DestinationFile);

        if(!reset_default && dest.exists())
        {
            dest.delete();
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

    /**
     * Button that transitions to leaving a message
     */
    protected void setButwr()
    {
        Button wr = findViewById(R.id.butwr);
        wr.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                if(msgr!=null)
                {
                    nArray = msgr.getNames();
                    mArray = msgr.getMsgs();
                    rArray = msgr.getRecip();
                    hArray = msgr.getHid();
                    eArray = msgr.getEd();
                }
                con1.removeAllViews();
                infl.inflate(R.layout.msg_lv, con1, true);
                setPpl();
                msgTxt = findViewById(R.id.postFrid);
                setRec();
                setButpst();
            }
        });
        setEd();
    }


    /**
     * Button that transitions out of leaving a message
     */
    protected void setButpst()
    {
        Button post = findViewById(R.id.msg_post);
        post.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                EditText e = findViewById(R.id.msg_input);

                String[] s = new String[3];
                s[0] = e.getText() + "";
                s[1] = "Posted from Fridge";
                s[2] = "Fridge";
                if(!Sen.isEmpty())
                {
                    s[2] = "";
                    for(int i = 0; i < Sen.size() - 1; i++)
                    {
                        s[2] += Sen.get(i) + ", ";
                    }
                    s[2] += Sen.get(Sen.size() - 1);
                }
                writ(s);
                con1.removeAllViews();
                infl.inflate(R.layout.msg_pg, con1, true);
                if(nArray != null) {
                    msgr = new cAdapter(contex, nArray, mArray, rArray, hArray, eArray);
                    listView = findViewById(R.id.msg_scr);
                    listView.setAdapter(msgr);
                }
                setButwr();
            }
        });
         setEdit();
    }

    protected void setMsg()
    {
            String[] s = readFile();

            int sz = s.length;
            String[] msgArr = Arrays.copyOfRange(s, 0, sz/3);
            String[] nameArr = Arrays.copyOfRange(s, sz/3, 2*sz/3);
            String[] recArr = Arrays.copyOfRange(s, 2*sz/3, sz);

            ArrayList<String> msgArray = new ArrayList<String>();
            ArrayList<String> nameArray = new ArrayList<String>();
            ArrayList<String> recArray = new ArrayList<String>();

            msgArray.addAll(Arrays.asList(msgArr));
            nameArray.addAll(Arrays.asList(nameArr));
            recArray.addAll(Arrays.asList(recArr));

            msgr = new cAdapter(this, nameArray, msgArray, recArray);

            listView = findViewById(R.id.msg_scr);
            listView.setAdapter(msgr);
    }

    protected String[] readFile()
    {
        FileInputStream is = null;
        Context context = getApplicationContext();
        String s;
        ArrayList<String> msgs = new ArrayList<String>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> recip = new ArrayList<String>();

        try {
            is = context.openFileInput("defmsg.txt");
            BufferedReader sc = new BufferedReader(new InputStreamReader(is));

            while((s = sc.readLine()) != null)
            {
                msgs.add(s);
                names.add(sc.readLine());
                recip.add(sc.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int sz = msgs.size();
        String[] msgbrd = new String[sz*3];
        for(int i = 0; i < sz; i++)
            msgbrd[i] = msgs.get(i);
        for(int i = sz; i < sz*2; i++)
            msgbrd[i] = names.get(i - sz);
        for(int i = sz*2; i < sz*3; i++)
            msgbrd[i] = recip.get(i - sz*2);
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

    public void writ(String[] s){
        if(!s[0].equals("")) {
            msgr.add(s[1], s[0], s[2]);
            msgr.notifyDataSetChanged();
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


    protected void setRec()
    {
        Button cr = findViewById(R.id.msgRec);
        cr.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                String[] opt = {"Add Recipient","Remove Recipient"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //builder.setTitle("Pick a color");
                builder.setItems(opt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // the user clicked on opt[i]
                        AlertDialog.Builder b2 = new AlertDialog.Builder(context);
                        if(i == 0)
                        {
                            String[] nSe = nSen.toArray(new String[0]);
                            if(nSe.length == 0)
                                b2.setTitle("No one to add");
                            else {
                                b2.setTitle("Add Recipient");
                                b2.setItems(nSe, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        // the user clicked on nSen[i]
                                        String s = nSen.get(i);
                                        nSen.remove(i);
                                        Sen.add(s);
                                        msgTxt.setText("Sending Direct Message");
                                    }
                                });
                            }
                        }
                        else
                        {
                            String[] Se = Sen.toArray(new String[0]);
                            if(Se.length == 0)
                                b2.setTitle("No one to remove");
                            else {
                                b2.setTitle("Remove Recipient");
                                b2.setItems(Se, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        // the user clicked on Sen[i]
                                        String s = Sen.get(i);
                                        Sen.remove(i);
                                        nSen.add(s);
                                        if(Sen.isEmpty())
                                            msgTxt.setText("Posting to Fridge");

                                    }
                                });
                            }
                        }
                        b2.show();
                    }

                });
                builder.show();
            }
        });
    }

    protected void setPpl()
    {
        String[] arr = {"Ana", "Dad", "Mom", "Tim"};
        nSen = new ArrayList<String>();
        Sen = new ArrayList<String>();
        nSen.addAll(Arrays.asList(arr));


    }

    protected void setEd()
    {
        if(listView == null)
            listView = findViewById(R.id.msg_scr);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id)
            {
                String[] opt1 = {"Delete","Edit","Hide"};
                String[] opt2 = {"Hide"};
                if(msgr.isHidden(pos))
                {
                    opt1[2] = "Unhide";
                    opt2[0] = "Unhide";
                }
                AlertDialog.Builder b2 = new AlertDialog.Builder(context);
                final int p = pos;
                if(msgr.getSender(pos).equals("Posted from Fridge")) {
                    b2.setItems(opt1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            // the user clicked on nSen[i]
                            if(i == 0)
                            {
                                AlertDialog.Builder al = new AlertDialog.Builder(context);

                                al.setTitle("Are you sure?");
                                al.setMessage("Message: " + msgr.getMsg(p));

                                // Set an EditText view to get user input

                                al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        msgr.delete(p);
                                    }
                                });
                                al.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Canceled.
                                    }
                                });
                                al.show();
                            }
                            else if(i == 1)
                            {
                                AlertDialog.Builder al = new AlertDialog.Builder(context);

                                al.setTitle("Edit Message");
                                // al.setMessage(msgr.getMsg(p));

                                // Set an EditText view to get user input
                                final EditText in = new EditText(context);

                                in.setText(msgr.getMsg(p), TextView.BufferType.EDITABLE);
                                al.setView(in);

                                al.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        msgr.edit(p,in.getText() + "");
                                    }
                                });
                                al.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Canceled.
                                    }
                                });

                                al.show();
                            }
                            else //Hide
                            {
                                msgr.hide(p);
                            }
                        }

                    });

                }
                else
                {
                    b2.setItems(opt2, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            // the user clicked on opt2[i]
                            if(i == 0)
                                msgr.hide(p);
                        }
                    });
                }
                b2.show();
                return true;
            }
        });
    }

    public void onBackPressed()
    {
        if(canBack)
            super.onBackPressed();
        else
        {
            con2.removeAllViews();
            infl.inflate(R.layout.shopping_list, con2, true);
            setShop();
        }
    }
}

