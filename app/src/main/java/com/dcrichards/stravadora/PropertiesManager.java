package com.dcrichards.stravadora;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Interface for pulling application properties from the properties file
 *
 * @author DCRichards
 */
public enum PropertiesManager {

    ENDPOINT("ENDPOINT"),
    ACCESSTOKEN("ACCESS_TOKEN"),
    STRAVAAUTHTOKEN("STRAVA_AUTH_TOKEN");

    private static final String TAG = "SD.PropManager";
    private static final String PROPERTIES_FILENAME = "stravadora.properties";

    private String key;

    PropertiesManager(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    /**
     * Get a specified property
     *
     * @param context   The Android context
     * @param property  The property key to get
     * @return The property value
     */
    public static String get(Context context, PropertiesManager property) {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(PROPERTIES_FILENAME);
            properties.load(inputStream);
            return properties.getProperty(property.getKey());
        } catch (IOException ioe) {
            Log.e(TAG, "exception reading file", ioe);
            return null;
        }
    }

}
