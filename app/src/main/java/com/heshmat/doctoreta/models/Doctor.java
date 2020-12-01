package com.heshmat.doctoreta.models;

public class Doctor  extends User{
    private String idCardUrl;
    private String medicalLicenseUrl;
    private String bio;
    private String speciality;
    public static Doctor currentLoggedDoctor;



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

    public Doctor() {
    }

    public Doctor(String id, String name, String phoneNumber, String email, String photoURL, String role) {
        super(id, name, phoneNumber, email, photoURL, role);
        this.setRole(StaticFields.DOCTOR_ROLE);
    }

    public Doctor(String id, String name, String phoneNumber, String email) {
        super(id, name, phoneNumber, email);
        this.setRole(StaticFields.DOCTOR_ROLE);

    }

}
