package com.dcrichards.stravadora;

import com.mapbox.mapboxsdk.geometry.LatLng;
import java.util.ArrayList;

/**
 * Represents an a Strava activity including route data
 *
 * @author DCRichards
 */
public class StravaActivity {

    private int id;
    private String name;
    private ArrayList<LatLng> route;
    private double distance;
    private double time;
    private String startDate;
    private String type;

    /**
     * Create a new Strava activity
     *
     * @param id        The id of the activity
     * @param name      The name of the activity
     * @param route     The lat lon points representing the route data
     * @param distance  The distance in meter
     * @param time      The time in seconds
     * @param startDate The start date in UNIX time
     * @param type      Activity type, 'Run' or 'Ride'
     */
    public StravaActivity(int id, String name, ArrayList<LatLng> route, double distance, double time, String startDate, String type) {
        this.id = id;
        this.name = name;
        this.route = route;
        this.distance = distance;
        this.time = time;
        this.startDate = startDate;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<LatLng> getRoute() {
        return route;
    }

    public double getDistance() {
        return distance;
    }

    public double getTime() {
        return time;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StravaActivity that = (StravaActivity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return type + " activity: " + name;
    }
}
