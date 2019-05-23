package com.example.sampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class ViewDoctorActivity extends AppCompatActivity implements OnMapReadyCallback {

    String email;
    String doctorEmail;
    FirebaseFirestore db;
    Doctor currentDoctor;
    PlacesClient pc;
    private GoogleMap map;

    int requestCode = 15;
    Place doctorLocation; //the Place of the doctor's address
    Place userLocation; //the Place of the user's address
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG); //fields needed from the places

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_doctor);

        Intent intent = getIntent();
        email = intent.getStringExtra(DoctorsActivity.EXTRA_EMAIL);
        doctorEmail = intent.getStringExtra(DoctorsActivity.EXTRA_DOCTOR_EMAIL);

        db = FirebaseFirestore.getInstance();

        //get the doctor's data from the db, then set the text accordingly.
        db.collection("users").document(email).collection("doctors").document(doctorEmail).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            currentDoctor = doc.toObject(Doctor.class);
                            setupText();
                            initLocations();
                        } else {
                            Log.w("ViewDoctorActivity", "Couldn't retrieve doctor data", task.getException());
                        }

                    }
                });

    }

    /** retrieves the user and doctor locations. Then, sets up the map widget */
    private void initLocations() {
        //here we retrieve the place represented by the doctor's address
        Places.initialize(getApplicationContext(), "AIzaSyDfU9D8p_AoXAQzATv_u90fX97LPtls55k");
        pc = Places.createClient(this);

        FetchPlaceRequest request = FetchPlaceRequest.builder(currentDoctor.placeId,fields).build();
        //fetch and store the doctor's Place
        pc.fetchPlace(request).addOnCompleteListener(new OnCompleteListener<FetchPlaceResponse>() {
            @Override
            public void onComplete(@NonNull Task<FetchPlaceResponse> task) {

                if(task.isSuccessful()) {
                    FetchPlaceResponse response = task.getResult();
                    doctorLocation = response.getPlace();
                    getUserLocation();
                } else {
                    Log.w("ViewDoctorActivity", "Couldn't get doctor's place.", task.getException());
                }

            }
        });

    }

    /** Retrieves and stores the current location of the user in the form of a place */
    private void getUserLocation() {

        //if the permission has been granted.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //start off by making a request to get the current place.
            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(fields).build();

            pc.findCurrentPlace(request)
                    .addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {

                            if(task.isSuccessful()) {

                                FindCurrentPlaceResponse response = task.getResult();
                                userLocation = getMaximumLikelihoodPlace(response.getPlaceLikelihoods());
                                initMap();

                            } else {
                                Log.w("ViewDoctorActivity", "Couldn't get user location", task.getException());
                            }

                        }
                    });

        } else {
            askForLocationPermission();
        }

    }

    /** The following code was placed in its own method so that it would only run on higher api's. */
    @TargetApi(23)
    private void askForLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
    }

    /** What is run after the user grants or denies a permission. */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            getUserLocation(); //we can get the user location now

        } else {
            //we don't end up activating the google map.
        }

    }

    /** Of a list of place likelihoods, returns the one with the highest likelihood */
    private Place getMaximumLikelihoodPlace(List<PlaceLikelihood> pls) {

        double max = -1.0;
        Place place = pls.get(0).getPlace(); //placeholder value (no pun intended)

        for(PlaceLikelihood likelihood : pls) {

            if(likelihood.getLikelihood() > max) {
                max = likelihood.getLikelihood();
                place = likelihood.getPlace();
            }

        }
        return place;
    }

    /** Initializes the TextViews in this activity. */
    private void setupText() {

        //get the elements.
        TextView nameEle = findViewById(R.id.output_doctorName);
        TextView emailEle = findViewById(R.id.output_doctorEmail);
        TextView numberEle = findViewById(R.id.output_doctorNumber);

        //set the texts.
        nameEle.setText(currentDoctor.name);
        emailEle.setText(currentDoctor.email);
        numberEle.setText(currentDoctor.phoneNumber);

    }

    /** Sets up Google Maps functionality */
    private void initMap() {
        Log.d("ViewDoctorActivity", userLocation.getAddress());
        Log.d("ViewDoctorActivity", doctorLocation.getAddress());

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }


}
