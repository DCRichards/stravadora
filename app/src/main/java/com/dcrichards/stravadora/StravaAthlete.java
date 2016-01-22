package com.dcrichards.stravadora;

import android.graphics.Bitmap;

/**
 * Represents a Strava athlete
 *
 * @author DCRichards
 */
public class StravaAthlete {

    private String firstname;
    private String lastname;
    private Bitmap profileImage;

    /**
     * Create a new strava athlete
     *
     * @param firstname     Athlete first name
     * @param lastname      Athlete surname
     * @param profileImage  Athlete profile image as a bitmap
     */
    public StravaAthlete(String firstname, String lastname, Bitmap profileImage) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.profileImage = profileImage;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }
}
