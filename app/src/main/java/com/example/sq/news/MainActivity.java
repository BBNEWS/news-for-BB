package com.example.sq.news;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private Button mbtn;
    private RecyclerView mRvContent;
    private EditText mRvText;
    private Button mbtn1;
    private Button mbtn2;
    private Button mbtn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mbtn =(Button)findViewById(R.id.news_btn);
        mRvContent=(RecyclerView)findViewById(R.id.rv_content);
        mRvText=(EditText)findViewById(R.id.rv_text);
        mbtn1=(Button)findViewById(R.id.btn1);
        mbtn2=(Button)findViewById(R.id.btn2);
        mbtn3=(Button)findViewById(R.id.btn3);
        mRvContent.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRvContent.setAdapter(new LinearAdapter(MainActivity.this));
    }
}
