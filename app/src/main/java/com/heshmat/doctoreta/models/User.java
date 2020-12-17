package com.heshmat.doctoreta.models;

import androidx.annotation.Nullable;

public class User {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private String photoURL;
    private String role;
    private long birthDate;
    private String gender;

    public static User currentLoggedUser;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public User() {
    }

    public long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
    }

    public User(String id, String name, String phoneNumber, String email, String photoURL, String role) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.photoURL = photoURL;
        this.role = role;
    }
    public User(String id, String name, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.photoURL = photoURL;
        this.role = role;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        User user= (User) obj;

        return this.id.equals(user.getId())&&this.getName().equals(user.getName());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode()+this.name.hashCode();
    }
}
