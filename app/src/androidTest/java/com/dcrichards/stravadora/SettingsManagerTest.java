package com.dcrichards.stravadora;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;

public class SettingsManagerTest extends ActivityInstrumentationTestCase2<MainActivity>  {

    MainActivity activity;
    Context mContext = null;

    public SettingsManagerTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() {
        activity = getActivity();
        mContext = activity;
    }

    public void testSetAndGetSettings() {
        SettingsManager.setMaxDataAge(mContext, 6);
        assertEquals("Max age doesn't match", 6, SettingsManager.getMaxDataAge(mContext));
        SettingsManager.setShowRun(mContext, false);
        assertEquals("Show run doesn't match", false, SettingsManager.getShowRun(mContext));
        SettingsManager.setShowCycle(mContext, true);
        assertEquals("Show cycle doesn't match", true, SettingsManager.getShowCycle(mContext));
        SettingsManager.setMapType(mContext, "dark");
        assertEquals("Map type doesn't match", "dark", SettingsManager.getMapType(mContext));
    }

}
