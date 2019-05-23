package com.example.sampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
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
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
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
    List<LatLng> path = new ArrayList<LatLng>();

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
        new RouteFinder().execute(0,0,0);

    }

    //the following class contains the code for displaying the route in the google map
    //the code is in an AsyncTask because the await call causes an exception when run on
    //the main thread. The integers are placeholder values
    private class RouteFinder extends AsyncTask<Integer,Integer,Integer> {

        @Override
        protected Integer doInBackground(Integer ... ints) {

            //start by setting up the directions API request
            GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyDfU9D8p_AoXAQzATv_u90fX97LPtls55k").build();
            String userCoords = String.valueOf(userLocation.getLatLng().latitude) + "," +  String.valueOf(userLocation.getLatLng().longitude);
            String doctorCoords = String.valueOf(doctorLocation.getLatLng().latitude) + "," +  String.valueOf(doctorLocation.getLatLng().longitude);
            DirectionsApiRequest req = DirectionsApi.getDirections(context, userCoords, doctorCoords);

            //loop through legs and steps to get encoded polylines of each step
            try {

                DirectionsResult result = req.await();

                //if there are routes, use the first one.
                if(result.routes != null && result.routes.length > 0) {
                    DirectionsRoute route = result.routes[0];

                    //if there are legs, do the following:
                    if(route.legs != null) {
                        for(int i = 0; i<route.legs.length; i++) {
                            DirectionsLeg leg = route.legs[i];

                            //if there are steps, do the following:
                            if(leg.steps != null) {

                                for(int j = 0; j<leg.steps.length; j++) {

                                    DirectionsStep step = leg.steps[j];
                                    //get the details within each step.
                                    if(step.steps != null && step.steps.length > 0) {

                                        for(int k = 0; k<step.steps.length; k++) {
                                            DirectionsStep step1 = step.steps[k];
                                            EncodedPolyline points1 = step1.polyline;

                                            if(points1 != null) {
                                                //decode polyline and add points to list
                                                List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                                for(com.google.maps.model.LatLng coord1 : coords1) {
                                                    path.add(new LatLng(coord1.lat,coord1.lng));
                                                }

                                            }

                                        }

                                    } else {
                                        //if we can't get the step details, proceed
                                        //with  adding the points to the list
                                        EncodedPolyline points = step.polyline;
                                        if(points != null) {
                                            //decode polyline and add points to list
                                            List<com.google.maps.model.LatLng> coords = points.decodePath();
                                            for (com.google.maps.model.LatLng coord : coords) {
                                                path.add(new LatLng(coord.lat, coord.lng));
                                            }
                                        }
                                    }

                                }

                            }

                        }

                    }

                }

            } catch(Exception e) {
                Log.e("ViewDoctorActivity", e.toString(), e);
            }
            return new Integer(1);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            //the following code displays the line.
            if(path.size() > 0) {
                PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
                map.addPolyline(opts);
            }

            map.getUiSettings().setZoomControlsEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation.getLatLng(), 6));
        }
    }

}
