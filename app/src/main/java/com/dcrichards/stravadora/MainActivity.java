package com.dcrichards.stravadora;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SD.MainActivity";
    private StravaClient strava;
    private StravadoraMap map;
    private ProgressDialog loadingDialog;
    private Dialog filterDialog;
    private DrawerLayout drawer;
    private HashMap<Integer, StravaActivity> cachedActivities = new HashMap<>();
    private StravaActivity currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        map = new StravadoraMap((MapView) findViewById(R.id.mapview));
        map.create(this.getApplicationContext(), savedInstanceState);
        strava = new StravaClient(this.getApplicationContext());
        setupClickListeners();
        getProfileInfo();
        updateMap();
    }

    private void setupClickListeners() {
        FloatingActionButton menuButton = (FloatingActionButton) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuButtonClicked();
            }
        });
        FloatingActionButton filterButton = (FloatingActionButton) findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFilterButtonClicked();
            }
        });
        map.setMarkerClickListener(new MapView.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                onMarkerClicked(marker);
                //event consumed so don't show popup
                return true;
            }
        });
    }

    private void updateMap() {
        showLoading();
        strava.getActivities(new StravaCallback<ArrayList<StravaActivity>>() {
            @Override
            public void onResult(ArrayList<StravaActivity> result) {
                for (StravaActivity act : result) {
                    // update cache - we add a mapping for easy getting on marker click
                    cachedActivities.put(act.getId(), act);
                    map.addRoute(act);
                }
                loadingDialog.cancel();
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "error updating map", ex);
            }
        });
    }

    private void getProfileInfo() {
        strava.getAthlete(new StravaCallback<StravaAthlete>() {
            @Override
            public void onResult(StravaAthlete result) {
                ((ImageView) findViewById(R.id.athleteImage)).setImageBitmap(result.getProfileImage());
                String name = result.getFirstname() + " " + result.getLastname();
                ((TextView) findViewById(R.id.athleteName)).setText(name);
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "error updating map", ex);
            }
        });
    }

    private void showLoading() {
        loadingDialog = new ProgressDialog(MainActivity.this);
        loadingDialog.setMessage(getResources().getString(R.string.map_data_loading));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void onMenuButtonClicked() {
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT);
        } else {
            drawer.openDrawer(Gravity.LEFT);
        }
    }

    private void onMarkerClicked(Marker marker) {
        showLoading();
        // reset currently highlighted and set new current activity
        if (currentActivity != null) {
            map.addRoute(currentActivity);
        }
        currentActivity = cachedActivities.get(Integer.parseInt(marker.getTitle()));
        // add highlight to map
        map.highlightRoute(currentActivity);
        // show snackbar
        Snackbar snackbar = Snackbar.make(findViewById(R.id.snackbarPosition), "", Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        snackbarLayout.findViewById(android.support.design.R.id.snackbar_text).setVisibility(View.INVISIBLE);
        View customSnackbarLayout = getLayoutInflater().inflate(R.layout.snackbar_activity_view, null);
        ((TextView) customSnackbarLayout.findViewById(R.id.activityTitle)).setText(currentActivity.getName());
        ((TextView) customSnackbarLayout.findViewById(R.id.activityDate)).setText(Utils.utcTimeToString(currentActivity.getStartDate()));
        ImageView type = (ImageView) customSnackbarLayout.findViewById(R.id.typeImage);
        if (currentActivity.getType().equals("Run")) {
            type.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_run_black_48dp));
        } else {
            type.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bike_black_48dp));
        }
        String distanceString = Utils.convertMetersToKm((long)currentActivity.getDistance())+ " in " + Utils.convertSecondsToHMmSs((long)currentActivity.getTime());
        ((TextView) customSnackbarLayout.findViewById(R.id.activityDistance)).setText(distanceString);
        snackbarLayout.addView(customSnackbarLayout);
        snackbar.show();
        loadingDialog.cancel();
    }

    private void onFilterButtonClicked() {
        filterDialog = new Dialog(MainActivity.this);
        filterDialog.setTitle(getResources().getString(R.string.filter_dialog_title));
        View dialogLayout = this.getLayoutInflater().inflate(R.layout.filter_dialog_layout,null);
        filterDialog.setContentView(dialogLayout);
        filterDialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            // Handle the camera action
        } else if (id == R.id.nav_activities) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        map.getMapView().onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.getMapView().onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.getMapView().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        map.getMapView().onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.getMapView().onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.getMapView().onSaveInstanceState(outState);
    }
}
