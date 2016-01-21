package com.dcrichards.stravadora;

/**
 * Callback for Strava API calls
 *
 * @param <T> Callback type for returning data
 */
public interface StravaCallback<T> {

    void onResult(T result);

    void onError(Exception ex);

}
