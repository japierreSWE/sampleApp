package com.example.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**takes user to page 1 when button1 is pressed*/
    public void toPage1(View view) {
        Intent intent = new Intent(this, Page1Activity.class);
        startActivity(intent);
    }

    /**takes user to page 2 when button2 is pressed*/
    public void toPage2(View view) {
        Intent intent = new Intent(this, Page2Activity.class);
        startActivity(intent);
    }

    /**takes user to page 3 when button3 is pressed*/
    public void toPage3(View view) {
        Intent intent = new Intent(this, Page3Activity.class);
        startActivity(intent);
    }

}
