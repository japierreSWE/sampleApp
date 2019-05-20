package com.example.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DoctorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        Intent intent = getIntent();
        String email = intent.getStringExtra(MainActivity.EXTRA_EMAIL);

        Log.d("DoctorsActivity", email);
    }
}
