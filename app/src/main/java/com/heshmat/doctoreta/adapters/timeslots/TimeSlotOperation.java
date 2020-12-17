package com.heshmat.doctoreta.adapters.timeslots;


import java.util.HashMap;

public  class TimeSlotOperation {
   public HashMap<String, HashMap<String, String>> timeSlot;
    public int type;

    public TimeSlotOperation(HashMap<String, HashMap<String, String>> timeSlot, int type) {
        this.timeSlot = timeSlot;
        this.type = type;
    }
}