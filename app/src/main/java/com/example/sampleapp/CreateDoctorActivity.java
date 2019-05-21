package com.example.sampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class CreateDoctorActivity extends AppCompatActivity {

    String email;
    String selectedPlaceAddress;
    String selectedPlaceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_doctor);
        email = getIntent().getStringExtra(DoctorsActivity.EXTRA_EMAIL);

        Places.initialize(getApplicationContext(), "AIzaSyDfU9D8p_AoXAQzATv_u90fX97LPtls55k");
        AutocompleteSupportFragment searchBar = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        searchBar.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));

        searchBar.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                selectedPlaceAddress = place.getAddress();
                selectedPlaceId = place.getId();
                Log.d("CreateDoctorActivity", selectedPlaceAddress + " " + selectedPlaceId);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.w("CreateDoctorActivity", "An error occurred " + status);
            }
        });

    }
}
