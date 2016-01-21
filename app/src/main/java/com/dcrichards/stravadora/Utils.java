package com.dcrichards.stravadora;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {

    private static final String TAG = "SD.Utils";

    public static String utcTimeToString(String timestring) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        try {
            return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(inputDateFormat.parse(timestring));
        } catch (ParseException pe) {
            Log.e(TAG, "unable to parse date", pe);
            return null;
        }
    }

    public static String convertMetersToKm(long meters) {
        return String.format("%.2f km", (float)meters/1000);
    }

    public static String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h,m,s);
    }



}
