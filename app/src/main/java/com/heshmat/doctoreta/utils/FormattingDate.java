package com.heshmat.doctoreta.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
    public static HashMap<String,String> getDaysOfWeek(Date refDate, int firstDayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(refDate);
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        HashMap<String,String> dateHashMap=new HashMap<>();
        for (int i = 0; i < 7; i++) {
            calendar.get(Calendar.DAY_OF_WEEK);
            new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
            dateHashMap.put(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime()),
                    new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH).format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dateHashMap;
    }
}
