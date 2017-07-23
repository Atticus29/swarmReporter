package com.example.guest.iamhere.models;

public class User {
    private String password;
    private String email;
    private String userName;
    private String phoneNumber;
    private Boolean contactOk;

    public User(String email, String userName, String phoneNumber, Boolean contactOk) {
        this.email = email;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.contactOk = contactOk;
    }

    public User(){}

    public void setContactOk(Boolean contactOk) {
        this.contactOk = contactOk;
    }

    public Boolean getContactOk() {
        return contactOk;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
