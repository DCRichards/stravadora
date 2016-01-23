# Stravadora

Stravadora is a location-centric Strava client for Android. Stravadora provides a geographic pespective of a user's Strava activities, displaying routes, distance and time data and allows filtering by a number of criteria. The name is derived from the Spanish for explorer - *exploradora*.

![screenshot](https://raw.githubusercontent.com/DCRichards/stravadora/master/screenshot/screenshot.png)

## Setup

In order to build from source, you must provide a `stravadora.properties` file in the `app/src/main/assets` directory. This should contain the [Strava API endpoint](http://strava.github.io/api/), A [Mapbox Access Token](https://www.mapbox.com/account/apps/) and a [Strava Access Token](https://www.strava.com/settings/api). For example:

	ENDPOINT = https://www.strava.com/api/v3
	STRAVA_AUTH_TOKEN = xxxxxxxxxxxxxxxx
	ACCESS_TOKEN = pk.xxxxxxxxxxxxxxxxx

## Build & Install

Build using Android Studio or run:

	gradle assembleDebug
	adb install app/build/outputs/apk/app-debug.apk

## Testing


## Libraries & Resources

Stravadora makes use of the following libraries:

* [Mapbox Android SDK](https://www.mapbox.com/android-sdk/). Note that the latest development snapshot is used here for testing the latest features and may be subject to change.
* [Android Volley](http://developer.android.com/training/volley/index.html) for an excellent async HTTP library with extensive data type support.
* [JODA Time](http://www.joda.org/joda-time/) because... well, Java date/time.
* [Android Material Design Icons](https://design.google.com/icons/)

## Todo

* Support Strava OAuth flow for login based access to API.