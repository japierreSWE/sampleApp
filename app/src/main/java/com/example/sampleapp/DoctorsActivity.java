package com.example.sampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
    public static final String EXTRA_DOCTOR_EMAIL = "com.example.sampleapp.DOCTOR_EMAIL";

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

        //config recycler view and its layout
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new DoctorAdapter(doctorsList);
        recyclerView.setAdapter(adapter);
        //config layout
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerClickListener() {
            @Override
            public void onClick(View view, int position) {
                TextView ele = view.findViewById(R.id.doctorName);
                String text = ele.getText().toString();
                Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                TextView ele = view.findViewById(R.id.doctorName);
                String text = ele.getText().toString();
                Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT).show();
            }
        }));

        //retrieve the doctors for this user.
        doctors.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()) {

                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                doctorsList.add(doc);
                            }

                            adapter.notifyDataSetChanged();

                        } else {
                            Log.w("DoctorsActivity", "Error getting doctors", task.getException());
                        }

                    }
                });

    }

    /** Goes to the Create Doctor activity */
    public void toCreateDoctor(View view) {
        Intent intent = new Intent(this, CreateDoctorActivity.class);
        intent.putExtra(EXTRA_EMAIL, email);
        startActivity(intent);
    }

    public static void toViewDoctor(int position) {

    }

}
