package com.example.sampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class CreateDoctorActivity extends AppCompatActivity {

    String userEmail;
    String selectedPlaceId;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_doctor);
        userEmail = getIntent().getStringExtra(DoctorsActivity.EXTRA_EMAIL);

        db = FirebaseFirestore.getInstance();
        Places.initialize(getApplicationContext(), "AIzaSyDfU9D8p_AoXAQzATv_u90fX97LPtls55k");
        AutocompleteSupportFragment searchBar = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        //we want these fields from the place.
        searchBar.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));

        //if we got the place, store its ID.
        searchBar.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                selectedPlaceId = place.getId();
                Log.d("CreateDoctorActivity",  selectedPlaceId);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.w("CreateDoctorActivity", "An error occurred " + status);
            }
        });

    }

    /** Adds a doctor to the user's data. */
    public void addDoctor(View view) {

        //retrieve all the elements
        EditText doctorNameEle = findViewById(R.id.create_doctorName);
        EditText doctorNumberEle = findViewById(R.id.create_doctorNumber);
        EditText doctorEmailEle = findViewById(R.id.create_doctorEmail);

        //get their inputs.
        String name = doctorNameEle.getText().toString();
        String number = doctorNumberEle.getText().toString();
        String email = doctorEmailEle.getText().toString();

        //if input is missing, stop and make a toast instead.
        if(selectedPlaceId == null || name.equals("") || number.equals("") || email.equals("")) {

            Toast.makeText(getApplicationContext(), "Please fill in all the fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Doctor doctor = new Doctor(name, selectedPlaceId, number, email);

        //add the doctor to a document in the user's doctors folder. document name is doctor's email.
        db.collection("users").document(userEmail).collection("doctors").document(email).set(doctor)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {

                            goToDoctors();

                        } else {

                            Log.w("CreateDoctorActivity", "Couldn't add doctor to db");
                            Toast.makeText(getApplicationContext(), "An internal error occurred. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    /**goes to the doctor activity*/
    private void goToDoctors() {

        finish();

    }

}
