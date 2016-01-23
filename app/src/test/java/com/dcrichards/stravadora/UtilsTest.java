package com.dcrichards.stravadora;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test
    public void testUtcTimeToString() throws Exception {
        String date = Utils.utcTimeToString("2012-12-13T03:43:19Z");
        assertEquals("Dates not equal", "13 Dec 2012", date);
        String date2 = Utils.utcTimeToString("foobar");
        assertEquals("Invalid date did not return null", null, date2);
    }

    @Test
    public void testConvertMetersToKm() throws Exception {
        String km1 = Utils.convertMetersToKm(1500);
        assertEquals("Distance not equal", "1.50", km1);
        String km2 = Utils.convertMetersToKm(0);
        assertEquals("Distance not equal", "0.00", km2);
        String km3 = Utils.convertMetersToKm(-1000);
        assertEquals("Distance not equal", "-1.00", km3);
    }

    @Test
    public void testConvertSecondsToHMmSs() throws Exception {
        String hms = Utils.convertSecondsToHMmSs(5423);
        assertEquals("Time not equal", "1:30:23", hms);
        String hms2 = Utils.convertSecondsToHMmSs(0);
        assertEquals("Time not equal", "0:00:00", hms2);
    }
}