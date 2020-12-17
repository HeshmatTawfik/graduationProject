package com.heshmat.doctoreta.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class TimeSlot {
    private String date;

    public HashMap<String, String> getSlots() {
        return slots;
    }

    public void setSlots(HashMap<String, String> slots) {
        this.slots = slots;
    }

    private HashMap<String, String> slots;
    boolean dateHasFreeSlot = false;

    public TimeSlot(String date, HashMap<String, String> slots) {
        this.date = date;
        this.slots = slots;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static ArrayList<TimeSlot> timeSlots(HashMap<String, HashMap<String, String>> doctorSlots) {
        ArrayList<TimeSlot> arrayList = new ArrayList<TimeSlot>();
        HashMap<String, HashMap<String, String>> filteredSlots = filter(doctorSlots);
        for (String key : filteredSlots.keySet()) {
            if (filteredSlots.get(key) != null && !Objects.requireNonNull(filteredSlots.get(key)).isEmpty()) {
                TimeSlot timeSlot = new TimeSlot(key, filteredSlots.get(key));

                arrayList.add(timeSlot);
            }

        }
        return arrayList;
    }

    private static HashMap<String, HashMap<String, String>> filter(HashMap<String, HashMap<String, String>> doctorSlots) {
        HashMap<String, HashMap<String, String>> filtered = new HashMap<String, HashMap<String, String>>();

        try {
            Calendar dateNow = Calendar.getInstance();
            Calendar reser = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            for (String date : doctorSlots.keySet()) {
                reser.setTime(dateFormat.parse(date));
                if (doctorSlots.get(date) != null) {
                    if (reser.get(Calendar.DAY_OF_MONTH) >= dateNow.get(Calendar.DAY_OF_MONTH) && reser.get(Calendar.MONTH) >= dateNow.get(Calendar.MONTH)) {
                        HashMap<String, String> dateSlot = new HashMap<>();
                        if (doctorSlots.get(date) != null)
                            for (String slot : doctorSlots.get(date).keySet()) {
                                {
                                    String[] startTimeStr = slot.split("-");
                                    reser.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeStr[0].split(":")[0]));
                                    reser.set(Calendar.MINUTE, Integer.parseInt(startTimeStr[0].split(":")[1]));
                                    String isFree = doctorSlots.get(date).get(slot);
                                    if (doctorSlots.get(date).get(slot).equals("free")) {
                                        String ss = doctorSlots.get(date).get(slot);
                                        if (reser.get(Calendar.DAY_OF_MONTH) == dateNow.get(Calendar.DAY_OF_MONTH) && reser.get(Calendar.MONTH) == dateNow.get(Calendar.MONTH)) {
                                            if (reser.after(dateNow)) {
                                                dateSlot.put(slot, "free");
                                            }
                                        } else {
                                            dateSlot.put(slot, "free");
                                        }
                                    }

                                }
                            }
                        if (!dateSlot.isEmpty())
                            filtered.put(date, dateSlot);
                    }
                }

            }
        } catch (Exception e) {
            return filtered;
        }
        return filtered;
    }
}
