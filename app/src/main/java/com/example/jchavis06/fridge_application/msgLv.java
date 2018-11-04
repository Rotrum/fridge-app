package com.example.jchavis06.fridge_application;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.jchavis06.fridge_application.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class msgLv extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrame = findViewById(R.id.content1);
        getLayoutInflater().inflate(R.layout.msg_lv, contentFrame, true);
        //TextView title = findViewById(R.id.title);
        //title.setText("Leave a Message");
        Button wr = findViewById(R.id.msg_post);
        wr.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                EditText e = findViewById(R.id.msg_input);
                String s = e.getText() + "\nYou\n";
                writ(getFilesDir().getPath() + File.separator, "defmsg.txt", s);
                finish();
            }
        });
        setEdit();
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
}
