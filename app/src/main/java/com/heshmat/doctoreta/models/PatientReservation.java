package com.heshmat.doctoreta.models;

public class PatientReservation extends Reservation {
    private String doctorId;
    private String doctorName;
    private String doctorEmail;
    private double price;

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }



    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public PatientReservation() {
    }

    public PatientReservation(String date, String status, String time, String doctorId, String doctorName, String doctorEmail, double price) {
        super(date, status, time);
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorEmail = doctorEmail;
        this.price = price;
    }
}
