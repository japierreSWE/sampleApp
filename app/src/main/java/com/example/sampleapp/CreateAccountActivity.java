package com.example.sampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Locale;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /** Makes a toast show up.
     * @param msg The message that should display */
    private void makeToast(String msg) {
        Toast.makeText(getApplicationContext(), msg,
                Toast.LENGTH_SHORT).show();
    }

    /** Handles a request to create an account */
    public void createAccount(View view) {

        //retrieve input elements.
        EditText emailEle = findViewById(R.id.create_email);
        EditText nameEle = findViewById(R.id.create_name);
        EditText dobMonthEle = findViewById(R.id.create_dobMonth);
        EditText dobDayEle = findViewById(R.id.create_dobDay);
        EditText dobYearEle = findViewById(R.id.create_dobYear);
        EditText passwordEle = findViewById(R.id.create_pass);
        EditText confirmPasswordEle = findViewById(R.id.create_confirmPass);

        //then retrieve the input from each one.
        String email = emailEle.getText().toString();
        String name = nameEle.getText().toString();
        String dobMonth = dobMonthEle.getText().toString();
        String dobDay = dobDayEle.getText().toString();
        String dobYear = dobYearEle.getText().toString();
        String password = passwordEle.getText().toString();
        String confirm = confirmPasswordEle.getText().toString();

        //validate password input.
        if(password.length() < 6) {
            makeToast("Passwords should be at least 6 characters long.");
            return;
        }

        if(!password.equals(confirm)) {
            makeToast("Password and confirm password aren't the same.");
            return;
        }

        String dob = dobYear + "-" + dobMonth + "-" + dobDay; //make the dob in SQL date format.

        //validate date input.
        try {
            new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dob);
        } catch(ParseException e) {
            makeToast("Invalid date input.");
            return;
        }

        //after validating all the input, create a new user.
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //if the account was created, go back to start page.
                        if(task.isSuccessful()) {
                            Log.d("CreateAccountActivity", "Made user");
                        } else {
                            makeToast("Unable to create account.");
                        }

                    }
                });

        User user = new User(email, dob, name, password);

        //add the user's info to the database.
        db.collection("users").document(email).set(user)
                //go back to start page if successful.
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToast("Internal error. Please try again.");
                        Log.w("CreateAccountActivity", "Couldn't write document", e);
                    }
                });

    }

}
