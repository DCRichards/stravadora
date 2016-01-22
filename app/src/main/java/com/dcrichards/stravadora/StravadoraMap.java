package com.dcrichards.stravadora;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.constants.MyLocationTracking;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.Collection;
import java.util.HashMap;

public class StravadoraMap {

    private static final String TAG = "SD.Map";

    private MapView map;
    private HashMap<Integer, Polyline> currentRoutes = new HashMap<>();
    private int routeColor;
    private int highlightColor;

    public StravadoraMap(MapView map) {
        this.map = map;
    }

    public void create(Context context, Bundle savedInstanceState) {
        map.setAccessToken(PropertiesManager.get(context, PropertiesManager.ACCESSTOKEN));
        String mapType = SettingsManager.getMapType(context);
        setMapType(mapType);
        map.setLatLng(new LatLng(50.8429, -0.13777));
        try {
            map.setMyLocationEnabled(true);
            map.setMyLocationTrackingMode(MyLocationTracking.TRACKING_FOLLOW);
        } catch (SecurityException se) {
            Log.e(TAG, "no manifest permission found for location", se);
        }
        map.setZoom(12);
        map.onCreate(savedInstanceState);
    }

    public MapView getMapView() {
        return this.map;
    }

    public void setMapType(String mapType) {
        switch (mapType) {
            case "dark":
                map.setStyleUrl(Style.DARK);
                routeColor = Color.parseColor("#ff8c1a");
                highlightColor = Color.WHITE;
                break;
            case "satellite":
                map.setStyleUrl(Style.SATELLITE_STREETS);
                routeColor = Color.WHITE;
                highlightColor = Color.BLUE;
                break;
            case "streets":
                map.setStyleUrl(Style.MAPBOX_STREETS);
                routeColor = Color.GRAY;
                highlightColor = Color.RED;
                break;
        }
    }

    public void clearMap() {
        map.removeAllAnnotations();
    }

    public Marker addMarker(LatLng position, int id) {
        MarkerOptions opts = new MarkerOptions();
        opts.position(position);
        opts.title("" + id);
        return map.addMarker(opts);
    }

    public void setMarkerClickListener(MapView.OnMarkerClickListener markerClickListener) {
        map.setOnMarkerClickListener(markerClickListener);
    }

    public void highlightRoute(StravaActivity activity) {
        map.removeAnnotation(currentRoutes.remove(activity.getId()));
        PolylineOptions route = new PolylineOptions();
        route.width(5);
        route.color(highlightColor);
        Polyline line = map.addPolyline(route.addAll(activity.getRoute()));
        currentRoutes.put(activity.getId(), line);
    }

    public void addRoutes(Collection<StravaActivity> activities) {
        for (StravaActivity act: activities) {
            addRoute(act);
        }
    }

    public void addRoute(StravaActivity activity) {
        map.removeAnnotation(currentRoutes.remove(activity.getId()));
        PolylineOptions route = new PolylineOptions();
        route.width(5);
        route.alpha((float) 0.5);
        route.color(routeColor);
        addMarker(activity.getRoute().get(0), activity.getId());
        Polyline line = map.addPolyline(route.addAll(activity.getRoute()));
        currentRoutes.put(activity.getId(),line);
    }

}
