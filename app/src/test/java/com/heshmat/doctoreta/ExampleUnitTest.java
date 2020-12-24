package com.heshmat.doctoreta;

import android.os.Handler;

import com.heshmat.doctoreta.models.Reservation;
import com.heshmat.doctoreta.utils.FakeDoctors;
import com.heshmat.doctoreta.utils.FormattingDate;
import com.heshmat.doctoreta.utils.NTPServerConnect;
import com.heshmat.doctoreta.utils.NTPServerListener;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    public void addition_isCorrect() {
        FakeDoctors.main();
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testDateFormat() {
        System.out.println("hello");
        Calendar[] calendars = FormattingDate.reservationDateFormatted("2020-12-21", "17:19-20:06");
        System.out.println(String.format("start date %s start time %d:%d ", calendars[0].getTime().toString(), calendars[0].get(Calendar.HOUR_OF_DAY)
                , calendars[0].get(Calendar.MINUTE)));

        System.out.println(String.format("end date %s start time %d:%d ", calendars[1].getTime().toString(), calendars[1].get(Calendar.HOUR_OF_DAY)
                , calendars[1].get(Calendar.MINUTE)));
        Calendar now=Calendar.getInstance();
        System.out.println(Reservation.hasMeetingStarted(now,calendars));

    }

}