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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init firebase auth.
        mAuth = FirebaseAuth.getInstance();
    }

    /** Authenticates a email/pass */
    private void auth(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            goToDoctors();

                        } else {
                            //log the exception and make a toast if we couldn't authenticate.
                            Log.w("MainActivity", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();


                        }

                    }
                });

    }

    /** Handles a request to sign in */
    public void signIn(View view) {

        //retrieve the elements containing email and pass input
        EditText emailEle = findViewById(R.id.email);
        EditText passwordEle = findViewById(R.id.pass);
        //retrieve the input
        String emailInput = emailEle.getText().toString();
        String passwordInput = passwordEle.getText().toString();

        //attempt to authenticate with the input. if we did, go to doctors list page
        email = emailInput;
        auth(emailInput, passwordInput);
    }

    /** Goes to the Doctors activity */
    public void goToDoctors() {

        Intent intent = new Intent(this, DoctorsActivity.class);
        startActivity(intent);

    }

    /** Goes to the Create Account activity */
    public void goToCreate(View view) {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

}
