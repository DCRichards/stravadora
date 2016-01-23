package com.dcrichards.stravadora;

/**
 * Callback for Strava API calls
 *
 * @param <T> Callback type for returning data
 */
public interface StravaCallback<T> {

    /**
     * On result returned from action
     *
     * @param result Result of specified type
     */
    void onResult(T result);

    /**
     * On error returned from action
     *
     * @param ex The exception thrown
     */
    void onError(Exception ex);

}
