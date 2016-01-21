package com.dcrichards.stravadora;

import com.mapbox.mapboxsdk.geometry.LatLng;
import java.util.ArrayList;

public class StravaActivity {

    private int id;
    private String name;
    private ArrayList<LatLng> route;
    private double distance;
    private double time;
    private String startDate;
    private String type;

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
