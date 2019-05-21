package com.example.sampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DoctorsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ArrayList<QueryDocumentSnapshot> doctorsList;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    String email;

    public static final String EXTRA_EMAIL = "com.example.sampleapp.EMAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        Intent intent = getIntent();
        email = intent.getStringExtra(MainActivity.EXTRA_EMAIL);

        Log.d("DoctorsActivity", email);

        doctorsList = new ArrayList<QueryDocumentSnapshot>();
        db = FirebaseFirestore.getInstance();
        CollectionReference doctors = db.collection("users").document(email).collection("doctors");

        //retrieve the doctors for this user.
        doctors.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()) {

                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                doctorsList.add(doc);
                            }

                            configRecyclerView();

                        } else {
                            Log.w("DoctorsActivity", "Error getting doctors", task.getException());
                        }

                    }
                });

    }

    /** Initializes the recycler view */
    private void configRecyclerView() {

        recyclerView = findViewById(R.id.recyclerView);

        //config layout and adapter.
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DoctorAdapter(doctorsList);
        recyclerView.setAdapter(adapter);

    }

    /** Goes to the Create Doctor activity */
    public void toCreateDoctor(View view) {
        Intent intent = new Intent(this, CreateDoctorActivity.class);
        intent.putExtra(EXTRA_EMAIL, email);
        startActivity(intent);
    }
}
