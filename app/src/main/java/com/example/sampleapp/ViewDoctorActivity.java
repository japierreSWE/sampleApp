package com.example.sampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ViewDoctorActivity extends AppCompatActivity {

    String email;
    String doctorEmail;
    FirebaseFirestore db;
    Doctor currentDoctor;

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
                        } else {
                            Log.w("ViewDoctorActivity", "Couldn't retrieve doctor data", task.getException());
                        }

                    }
                });

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

}
