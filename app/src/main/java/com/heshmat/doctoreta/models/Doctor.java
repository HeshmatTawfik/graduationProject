package com.heshmat.doctoreta.models;

import com.google.firebase.firestore.Exclude;

public class Doctor  extends User{
    private String idCardUrl;
    private String medicalLicenseUrl;
    private String bio;
    private String speciality;
    private boolean isVerified;
    public static Doctor currentLoggedDoctor;
    private AddressInfo addressInfo;
    private String status;
    private double price;



    public String getIdCardUrl() {
        return idCardUrl;
    }

    public void setIdCardUrl(String idCardUrl) {
        this.idCardUrl = idCardUrl;
    }

    public String getMedicalLicenseUrl() {
        return medicalLicenseUrl;
    }

    public void setMedicalLicenseUrl(String medicalLicenseUrl) {
        this.medicalLicenseUrl = medicalLicenseUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public AddressInfo getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(AddressInfo addressInfo) {
        this.addressInfo = addressInfo;
    }

    public Doctor() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Doctor(String id, String name, String phoneNumber, String email, String photoURL, String role) {
        super(id, name, phoneNumber, email, photoURL, role);
        this.setRole(StaticFields.DOCTOR_ROLE);
    }

    public Doctor(String id, String name, String phoneNumber, String email) {
        super(id, name, phoneNumber, email);
        this.setRole(StaticFields.DOCTOR_ROLE);

    }

    public Doctor(String id, String name, String phoneNumber, String email, String photoURL, String role, String idCardUrl, String medicalLicenseUrl, String bio, String speciality, boolean isVerified, AddressInfo addressInfo, String status, double price) {
        super(id, name, phoneNumber, email, photoURL, role);
        this.idCardUrl = idCardUrl;
        this.medicalLicenseUrl = medicalLicenseUrl;
        this.bio = bio;
        this.speciality = speciality;
        this.isVerified = isVerified;
        this.addressInfo = addressInfo;
        this.status = status;
        this.price = price;
    }
    @Exclude
    public String generateBio(){
        return String.format("Doctor %s is a %s specialist ", this.getName(), this.getSpeciality());}
}
