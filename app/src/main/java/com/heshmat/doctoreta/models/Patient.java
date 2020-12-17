package com.heshmat.doctoreta.models;

public class Patient extends User{
    public Patient() {
    }

    public Patient(String id, String name, String phoneNumber, String email, String photoURL, String role) {
        super(id, name, phoneNumber, email, photoURL, role);
        this.setRole(StaticFields.PATIENT_ROLE);
    }

    public Patient(String id, String name, String phoneNumber, String email){
        super(id, name, phoneNumber, email);
        this.setRole(StaticFields.PATIENT_ROLE);

    }
}
