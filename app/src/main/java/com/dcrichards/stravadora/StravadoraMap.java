package com.dcrichards.stravadora;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.HashMap;

public class StravadoraMap {

    private MapView map;
    private HashMap<Integer, Polyline> currentRoutes = new HashMap<>();

    public StravadoraMap(MapView map) {
        this.map = map;
    }

    public void create(Context context, Bundle savedInstanceState) {
        map.setAccessToken(PropertiesManager.get(context, PropertiesManager.ACCESSTOKEN));
        map.setStyleUrl(Style.MAPBOX_STREETS);
        map.setLatLng(new LatLng(50.8429, -0.13777));
        map.setZoom(13);
        map.onCreate(savedInstanceState);
    }

    public MapView getMapView() {
        return this.map;
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
        route.color(Color.RED);
        Polyline line = map.addPolyline(route.addAll(activity.getRoute()));
        currentRoutes.put(activity.getId(), line);
    }

    public void addRoute(StravaActivity activity) {
        map.removeAnnotation(currentRoutes.remove(activity.getId()));
        PolylineOptions route = new PolylineOptions();
        route.width(5);
        route.alpha((float) 0.5);
        route.color(Color.GRAY);
        addMarker(activity.getRoute().get(0), activity.getId());
        Polyline line = map.addPolyline(route.addAll(activity.getRoute()));
        currentRoutes.put(activity.getId(),line);
    }

}
