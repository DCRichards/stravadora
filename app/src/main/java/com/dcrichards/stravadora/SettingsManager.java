package com.dcrichards.stravadora;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * An interface for persistent settings within the application
 *
 * @author DCRichards
 */
public class SettingsManager {

    private static final String PREFERENCE_FILE = "StravadoraPrefs";
    private static final String KEY_MAX_AGE = "MAX_AGE";
    private static final String KEY_SHOW_CYCLE = "SHOW_CYCLE";
    private static final String KEY_SHOW_RUN = "SHOW_RUN";
    private static final String KEY_MAP_TYPE = "MAP_TYPE";

    /**
     * Get the max age (in months) of data on the map
     *
     * @param context Current context
     *
     * @return The max age of data
     */
    public static int getMaxDataAge(Context context) {
        return context.getSharedPreferences(PREFERENCE_FILE, 0).getInt(KEY_MAX_AGE, 6);
    }

    /**
     * Set the max age of data on the map
     *
     * @param context Current context
     * @param age The age of data (in months)
     */
    public static void setMaxDataAge(Context context, int age) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE, 0).edit();
        editor.putInt(KEY_MAX_AGE, age);
        editor.apply();
    }

    /**
     * Get whether cycle data is displayed on map
     *
     * @param context Current context
     *
     * @return true if cycle data is shown
     */
    public static boolean getShowCycle(Context context) {
        return context.getSharedPreferences(PREFERENCE_FILE, 0).getBoolean(KEY_SHOW_CYCLE, true);
    }

    /**
     * Set whether cycle data is displayed on map
     *
     * @param context Current context
     * @param show Whether cycle data is shown
     */
    public static void setShowCycle(Context context, boolean show) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE, 0).edit();
        editor.putBoolean(KEY_SHOW_CYCLE, show);
        editor.apply();
    }

    /**
     * Get whether run data is displayed on map
     *
     * @param context Current context
     *
     * @return True is run data is shown
     */
    public static boolean getShowRun(Context context) {
        return context.getSharedPreferences(PREFERENCE_FILE, 0).getBoolean(KEY_SHOW_RUN, true);
    }


    /**
     * Set whether run data is displayed on the map
     *
     * @param context Current context
     * @param show Whether run data is shown
     */
    public static void setShowRun(Context context, boolean show) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE, 0).edit();
        editor.putBoolean(KEY_SHOW_RUN, show);
        editor.apply();
    }

    /**
     * Get the current map type
     *
     * @param context Current context
     * @return Current map type as a string
     */
    public static String getMapType(Context context) {
        return context.getSharedPreferences(PREFERENCE_FILE, 0).getString(KEY_MAP_TYPE, "streets");
    }

    /**
     * Set the current map type
     *
     * @param context Current context
     * @param type Map type string
     */
    public static void setMapType(Context context, String type) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE, 0).edit();
        editor.putString(KEY_MAP_TYPE, type);
        editor.apply();
    }

}
