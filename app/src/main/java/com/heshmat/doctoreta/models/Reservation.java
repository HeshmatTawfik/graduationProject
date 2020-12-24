package com.heshmat.doctoreta.models;

import com.google.firebase.firestore.Exclude;

import java.util.Calendar;

import androidx.annotation.Nullable;

public class Reservation {
    private String date;
    private String status;
    private String time;
    private String reservationId;
    private String imgUrl;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Reservation() {
    }



    @Exclude
    public String getReservationId() {
        return String.format("%s_%s",date,time);
    }

    @Override
    public int hashCode() {
        return getReservationId().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        return obj != null && ((Reservation) obj).getReservationId().equals(this.getReservationId());
    }
    @Exclude
    public static boolean hasMeetingStarted (Calendar now, Calendar[] timeArr){
        return (now.equals(timeArr[0])||(now.after(timeArr[0])&&now.before(timeArr[1])));
    }
    @Exclude
    public static boolean hasMeetingFinished (Calendar now, Calendar endTime){
        return (now.after(endTime));
    }
}
