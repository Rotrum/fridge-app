package com.example.jchavis06.fridge_application;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


public class msgPg extends AppCompatActivity {
    protected  cAdapter msgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FrameLayout contentFrame = findViewById(R.id.content1);
        getLayoutInflater().inflate(R.layout.msg_pg, contentFrame, true);
        //TextView title = findViewById(R.id.title);
        //title.setText("Messages");

        Button wr = findViewById(R.id.butwr);
        wr.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Intent myIntent = new Intent(getApplicationContext(), msgLv.class);
                startActivity(myIntent);
            }
        });

    setMsg();


    }

    protected void setMsg()
    {
        String[] s = readFile();
       // String[] s = readAsset();

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
        Context context = getApplicationContext();
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

    public void onResume()
    {
    setMsg();
    }

  /*  public void onRestart()
    {
        onResume();
    }*/
}
