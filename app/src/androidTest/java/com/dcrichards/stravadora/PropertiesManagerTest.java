package com.dcrichards.stravadora;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;

public class PropertiesManagerTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity activity;
    Context mContext = null;

    public PropertiesManagerTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() {
        activity = getActivity();
        mContext = activity;
    }

    public void testGetProperties() {
        assertNotNull(PropertiesManager.get(mContext, PropertiesManager.ENDPOINT));
        assertNotNull(PropertiesManager.get(mContext, PropertiesManager.STRAVAAUTHTOKEN));
        assertNotNull(PropertiesManager.get(mContext, PropertiesManager.ACCESSTOKEN));
    }

}
