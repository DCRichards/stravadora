package com.dcrichards.stravadora;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * An asynchronous interface for interacting with the Strava API
 *
 * @see <a href="http://strava.github.io/api/">Strava API V3</a>
 *
 * @author DCRichards
 */
public class StravaAPI {

    private static final String TAG = "SD.StravaAPI";

    private String endpoint;
    private String authToken;
    private RequestQueue requestQueue;

    public StravaAPI(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        endpoint = PropertiesManager.get(context, PropertiesManager.ENDPOINT);
        authToken = PropertiesManager.get(context, PropertiesManager.STRAVAAUTHTOKEN);
    }

    class CustomJSONArrayRequest extends JsonArrayRequest {

        public CustomJSONArrayRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + authToken);
            return headers;
        }
    }

    class CustomJSONObjectRequest extends JsonObjectRequest {

        public CustomJSONObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(url, jsonRequest, listener, errorListener);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + authToken);
            return headers;
        }

    }

    public void getProfileImage(String imageUrl, final StravaCallback<Bitmap> callback) {
        ImageRequest request = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                callback.onResult(response);
            }
        }, 0, 0, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                });
        requestQueue.add(request);
    }

    public void getAthlete(final StravaCallback<JSONObject> callback) {
        CustomJSONObjectRequest request = new CustomJSONObjectRequest(endpoint+"/athlete",null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        requestQueue.add(request);
    }

    public void getActivities(final StravaCallback<JSONArray> callback) {
        getActivities(-1, callback);
    }

    public void getActivities(final int since, final StravaCallback<JSONArray> callback) {
        String query = (since > 0) ? "/athlete/activities?after="+since : "/athlete/activities";
        CustomJSONArrayRequest request = new CustomJSONArrayRequest(endpoint + query, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callback.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        requestQueue.add(request);
    }

    public void getStreamForActivity(final int activityId, final StravaCallback<JSONArray> callback) {
        CustomJSONArrayRequest request = new CustomJSONArrayRequest(endpoint+"/activities/"+activityId+"/streams/latlng?resolution=low", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callback.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        requestQueue.add(request);
    }
}
