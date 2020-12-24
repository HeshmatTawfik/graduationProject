package com.heshmat.doctoreta.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class FormattingDate {
    public static String formattedDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        if (date != null) {
            String strDate = dateFormat.format(date);
            return strDate;
        }
        return "-";

    }

    public static int getAge(long dateInMillis) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.setTimeInMillis(dateInMillis);
        //  dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Log.i("getAge", "getAge: " + age);
        return age;
    }

    public static HashMap<String, String> getDaysOfWeek(Date refDate, int firstDayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(refDate);
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        HashMap<String, String> dateHashMap = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            calendar.get(Calendar.DAY_OF_WEEK);
            new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
            dateHashMap.put(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime()),
                    new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dateHashMap;
    }

    public static Calendar[] reservationDateFormatted(String dateStr, String hourStr) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        try {

            String[] startArr = hourStr.split("-")[0].split(":");
            String[] endArr = hourStr.split("-")[1].split(":");
            start.setTime(Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateStr)));
            start.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startArr[0]));
            start.set(Calendar.MINUTE, Integer.parseInt(startArr[1]));
            end.setTime(Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(dateStr)));
            end.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endArr[0]));
            end.set(Calendar.MINUTE, Integer.parseInt(endArr[1]));


        } catch (Exception e) {
            return null;

        }
        return new Calendar[]{start, end};

    }
}
