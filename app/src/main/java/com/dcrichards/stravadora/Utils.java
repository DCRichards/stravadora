package com.dcrichards.stravadora;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Useful utilities for use across the application
 *
 * @author DCRichards
 */
public class Utils {

    private static final String TAG = "SD.Utils";

    /**
     * Convert an ISO 8601 date time string to a formatted date string
     *
     * @param timestring ISO8601 compliant date time string
     *
     * @return Date string in the format dd MMM yyyy
     */
    public static String utcTimeToString(String timestring) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        try {
            return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(inputDateFormat.parse(timestring));
        } catch (ParseException pe) {
            Log.e(TAG, "unable to parse date", pe);
            return null;
        }
    }

    /**
     * Convert a distance in meters to kilometers as a string
     *
     * @param meters Distance in meters
     *
     * @return Distance in km as a string to 2dp
     */
    public static String convertMetersToKm(long meters) {
        return String.format("%.2f", (float)meters/1000);
    }

    /**
     * Convert time in seconds to formatted time string
     *
     * @param seconds Time in seconds
     *
     * @return A String in the format H:Mm:Ss
     */
    public static String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h,m,s);
    }

}
