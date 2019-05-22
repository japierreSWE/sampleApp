package com.example.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ViewDoctorActivity extends AppCompatActivity {

    String email;
    String doctorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_doctor);

        Intent intent = getIntent();
        email = intent.getStringExtra(DoctorsActivity.EXTRA_EMAIL);
        doctorEmail = intent.getStringExtra(DoctorsActivity.EXTRA_DOCTOR_EMAIL);
    }
}
