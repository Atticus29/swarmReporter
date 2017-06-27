package com.example.guest.iamhere.models;

public class User {
    private String password;
    private String email;
    private String userName;
    private String phoneNumber;

    public User(String email, String userName, String phoneNumber) {
        this.email = email;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
    }

    public User(){}
}
