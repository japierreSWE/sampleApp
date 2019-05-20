package com.example.sampleapp;

public class User {

    String email; //the user's email
    String dob; //the user's date of birth
    String name; //the user's name
    String password; //the user's password

    //no-arg
    public User() {

    }

    public User(String email, String dob, String name, String password) {

        this.email = email;
        this.dob = dob;
        this.name = name;
        this.password = password;

    }

}
