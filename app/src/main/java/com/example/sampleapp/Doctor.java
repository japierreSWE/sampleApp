package com.example.sampleapp;

public class Doctor {

    String name;
    String placeId;
    String phoneNumber;
    String email;

    //no-arg
    public Doctor() {

    }

    public Doctor(String name, String address, String phoneNumber, String email) {

        this.name = name;
        this.placeId = address;
        this.phoneNumber = phoneNumber;
        this.email = email;

    }

}
