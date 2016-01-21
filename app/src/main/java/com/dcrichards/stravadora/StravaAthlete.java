package com.dcrichards.stravadora;

import android.graphics.Bitmap;

public class StravaAthlete {

    private String firstname;
    private String lastname;
    private Bitmap profileImage;

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
