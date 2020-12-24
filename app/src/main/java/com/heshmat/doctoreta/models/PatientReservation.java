package com.heshmat.doctoreta.models;

import com.google.firebase.firestore.Exclude;

import java.util.Calendar;

import androidx.annotation.Nullable;

public class PatientReservation extends Reservation {

    private String reservationId;
    private String doctorId;
    private String doctorName;

    private String date;
    private String time;
    private String status;
    private double price;
    private Long createdAt;
    private String speciality;

    public Long getCreatedAt() {
        return createdAt;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

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




    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public PatientReservation(String doctorId, String doctorName, String speciality,String date, String time, String status, double price, long createdAt) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.speciality=speciality;
        this.date = date;
        this.time = time;
        this.status = status;
        this.price = price;
        this.createdAt = createdAt;
    }

    public PatientReservation() {
    }

    @Exclude
    public String getReservationId() {
        return String.format("%s_%s", date, time);
    }

    @Exclude
    @Override
    public int hashCode() {
        return getReservationId().hashCode();
    }

    @Exclude
    @Override
    public boolean equals(@Nullable Object obj) {

        return obj != null && ((PatientReservation) obj).getReservationId().equals(this.getReservationId());
    }

}
