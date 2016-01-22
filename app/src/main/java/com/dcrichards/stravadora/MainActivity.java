package com.dcrichards.stravadora;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SD.MainActivity";

    private StravaClient strava;
    private StravadoraMap map;
    private ProgressDialog loadingDialog;
    private Dialog filterDialog;
    private DrawerLayout drawer;
    private Snackbar activitySnackbar;
    private HashMap<Integer, StravaActivity> cachedActivities = new HashMap<>();
    private StravaActivity currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        checkLocationServicesEnabled();
        map = new StravadoraMap((MapView) findViewById(R.id.mapview));
        map.create(this.getApplicationContext(), savedInstanceState);
        strava = new StravaClient(this.getApplicationContext());
        setupClickListeners();
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
                // event consumed so don't show popup
                return true;
            }
        });
    }

    private void fetchActivityData() {
        showLoading();
        int maxAge = SettingsManager.getMaxDataAge(getApplicationContext());
        final boolean showRun = SettingsManager.getShowRun(getApplicationContext());
        final boolean showCycle = SettingsManager.getShowCycle(getApplicationContext());
        long timestamp = new DateTime(new Date()).minusMonths(maxAge).getMillis()/1000;
        strava.getActivities(timestamp, new StravaCallback<ArrayList<StravaActivity>>() {
            @Override
            public void onResult(ArrayList<StravaActivity> result) {
                cachedActivities.clear();
                currentActivity = null;
                if (activitySnackbar != null) {
                    activitySnackbar.dismiss();
                }
                // If we're not showing runs or cycle then don't bother iterating
                // otherwise, filter appropriately
                if (showRun || showCycle) {
                    for (StravaActivity act : result) {
                        if (act.getType().equals("Run") && showRun) {
                            cachedActivities.put(act.getId(), act);
                        } else if (act.getType().equals("Ride") & showCycle) {
                            cachedActivities.put(act.getId(), act);
                        }
                    }
                }
                refreshMap();
                loadingDialog.cancel();
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "error updating map", ex);
                loadingDialog.cancel();
            }
        });
    }

    private void refreshMap() {
        map.clearMap();
        map.addRoutes(cachedActivities.values());
        if (currentActivity != null) {
            map.highlightRoute(currentActivity);
        }
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
        // reset currently highlighted and set new current activity
        if (currentActivity != null) {
            map.addRoute(currentActivity);
        }
        currentActivity = cachedActivities.get(Integer.parseInt(marker.getTitle()));
        // add highlight to map
        map.highlightRoute(currentActivity);
        // show activity info to user
        showActivitySnackbar();
    }

    private void showActivitySnackbar() {
        activitySnackbar = Snackbar.make(findViewById(R.id.snackbarPosition), "", Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) activitySnackbar.getView();
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
        String distanceString = Utils.convertMetersToKm((long)currentActivity.getDistance())+ " km in " + Utils.convertSecondsToHMmSs((long)currentActivity.getTime());
        ((TextView) customSnackbarLayout.findViewById(R.id.activityDistance)).setText(distanceString);
        snackbarLayout.addView(customSnackbarLayout);
        activitySnackbar.show();
    }

    private void onFilterButtonClicked() {
        filterDialog = new Dialog(MainActivity.this);
        filterDialog.setTitle(getResources().getString(R.string.filter_dialog_title));
        // Get compontents
        View dialogLayout = this.getLayoutInflater().inflate(R.layout.filter_dialog_layout, null);
        final SeekBar ageSeeker = (SeekBar) dialogLayout.findViewById(R.id.ageBar);
        final TextView ageLabel = (TextView) dialogLayout.findViewById(R.id.ageValue);
        final CheckBox cycleCheck = (CheckBox) dialogLayout.findViewById(R.id.cycleCheckbox);
        final CheckBox runCheck = (CheckBox) dialogLayout.findViewById(R.id.runCheckbox);
        // Set values
        cycleCheck.setChecked(SettingsManager.getShowCycle(getApplicationContext()));
        runCheck.setChecked(SettingsManager.getShowRun(getApplicationContext()));
        int maxAge = SettingsManager.getMaxDataAge(getApplicationContext());
        // seek bar min is always 0 so offset value by 1 when setting retrieving
        ageSeeker.setProgress(maxAge - 1);
        ageLabel.setText(maxAge + (maxAge > 1 ? " Months" : " Month"));
        // Add listeners
        ageSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ageLabel.setText(progress + 1 + (progress+1 > 1 ? " Months" : " Month"));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        dialogLayout.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.cancel();
            }
        });
        dialogLayout.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveFilter(runCheck.isChecked(), cycleCheck.isChecked(), ageSeeker.getProgress() + 1);
            }
        });
        filterDialog.setContentView(dialogLayout);
        filterDialog.show();
    }

    private void onSaveFilter(boolean showRun, boolean showCycle, int maxAge) {
        SettingsManager.setShowRun(getApplicationContext(), showRun);
        SettingsManager.setShowCycle(getApplicationContext(), showCycle);
        SettingsManager.setMaxDataAge(getApplicationContext(), maxAge);
        filterDialog.cancel();
        fetchActivityData();
    }

    private void checkLocationServicesEnabled() {
        int locationStatus = 0;
        try {
            // Prior to Android 4.4 the setting is stored under a different constant
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                locationStatus = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } else {
                locationStatus = Settings.Secure.getInt(getContentResolver(),  Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            }
        } catch (Settings.SettingNotFoundException snfe) {
            Log.e(TAG, "Setting not found ", snfe);
        }
        if (locationStatus == Settings.Secure.LOCATION_MODE_OFF) {
            // prompt user to enable location settings
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            AlertDialog locationPrompt = builder.setTitle(getResources().getString(R.string.location_required))
                    .setMessage(getResources().getString(R.string.location_required_message))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getProfileInfo();
                            fetchActivityData();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getProfileInfo();
                            fetchActivityData();
                            dialog.dismiss();
                        }
                    })
                    .create();
            locationPrompt.show();
        } else {
            getProfileInfo();
            fetchActivityData();
        }
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_light) {
            SettingsManager.setMapType(getApplicationContext(), "streets");
            map.setMapType("streets");
        } else if (id == R.id.nav_dark) {
            SettingsManager.setMapType(getApplicationContext(), "dark");
            map.setMapType("dark");
        } else if (id == R.id.nav_satellite) {
            SettingsManager.setMapType(getApplicationContext(), "satellite");
            map.setMapType("satellite");
        }
        refreshMap();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "play services connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "play services connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Play services connection failed " + connectionResult.getErrorMessage());
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
