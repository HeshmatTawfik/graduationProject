package com.heshmat.doctoreta;

import com.heshmat.doctoreta.utils.FakeDoctors;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        FakeDoctors.main();
        assertEquals(4, 2 + 2);
    }
}