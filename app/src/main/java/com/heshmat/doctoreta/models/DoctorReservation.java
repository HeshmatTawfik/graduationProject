package com.heshmat.doctoreta.models;

public class DoctorReservation extends Reservation {
    private String clientId;
    private String clientName;

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



    public DoctorReservation(String date, String status, String time, String clientId, String clientName) {
        super(date, status, time);
        this.clientId = clientId;
        this.clientName = clientName;
    }

    public DoctorReservation() {
    }
}
