package com.dcrichards.stravadora;

import android.content.Context;
import android.graphics.Bitmap;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StravaClient {

    private StravaAPI api;

    public StravaClient(Context context) {
        api = new StravaAPI(context);
    }

    private ArrayList<LatLng> getPointsFromStream(JSONArray stream) throws JSONException {
        JSONObject streamObj = (JSONObject) stream.get(0);
        JSONArray streamArray = (JSONArray) streamObj.get("data");
        ArrayList<LatLng> points = new ArrayList<>();
        for (int i = 0; i < streamArray.length(); i++) {
            LatLng latlon = new LatLng((double)((JSONArray)streamArray.get(i)).get(0), (double)((JSONArray)streamArray.get(i)).get(1));
            points.add(latlon);
        }
        return points;
    }

    private void constructActivities(final JSONArray activityJSON, final StravaCallback<ArrayList<StravaActivity>> callback) {
        final ArrayList<StravaActivity> activitiesList = new ArrayList<>();
        for (int i = 0; i < activityJSON.length(); i++) {
            try {
                JSONObject activity = activityJSON.getJSONObject(i);
                final int id = activity.getInt("id");
                final String name = activity.getString("name");
                final String type = activity.getString("type");
                final double distance = activity.getDouble("distance");
                final double time = activity.getDouble("moving_time");
                final String startDate = activity.getString("start_date");
                api.getStreamForActivity(id, new StravaCallback<JSONArray>() {
                    @Override
                    public void onResult(JSONArray streamResult) {
                        try {
                            ArrayList<LatLng> route = getPointsFromStream(streamResult);
                            activitiesList.add(new StravaActivity(id, name, route, distance, time, startDate, type));
                            if (activityJSON.length() == activitiesList.size()) {
                                callback.onResult(activitiesList);
                            }
                        } catch (JSONException je) {
                            callback.onError(je);
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        callback.onError(ex);
                    }
                });
            } catch (JSONException je) {
                callback.onError(je);
            }
        }
    }

    public void getActivities(long since, final StravaCallback<ArrayList<StravaActivity>> callback) {
        api.getActivities(since, new StravaCallback<JSONArray>() {
            @Override
            public void onResult(final JSONArray activityResult) {
                constructActivities(activityResult, callback);
            }

            @Override
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });
    }

    public void getAthlete(final StravaCallback<StravaAthlete> callback) {
        api.getAthlete(new StravaCallback<JSONObject>() {
            @Override
            public void onResult(JSONObject result) {
                try {
                    final String firstname = result.getString("firstname");
                    final String lastname = result.getString("lastname");
                    String profileImageUrl = result.getString("profile_medium");
                    api.getProfileImage(profileImageUrl, new StravaCallback<Bitmap>() {
                        @Override
                        public void onResult(Bitmap result) {
                            callback.onResult(new StravaAthlete(firstname, lastname, result));
                        }

                        @Override
                        public void onError(Exception ex) {
                            callback.onError(ex);
                        }
                    });
                } catch (JSONException e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onError(Exception ex) {
                callback.onError(ex);
            }
        });
    }
}
