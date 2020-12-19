package com.heshmat.doctoreta.models;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.Nullable;

public class DoctorReservation extends Reservation {
    private String reservationId;
    private String clientId;
    private String clientName;
    private String date;
    private String time;
    private String status;
    private String email;
    private String phoneNumber;
    private long createdAt;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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



    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public DoctorReservation(String clientId, String clientName, String email, String phoneNumber,  String date, String time, String status, long createdAt) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.date = date;
        this.time = time;
        this.status = status;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.createdAt = createdAt;
    }

    public DoctorReservation() {

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

        return obj != null && ((DoctorReservation) obj).getReservationId().equals(this.getReservationId());
    }
}
