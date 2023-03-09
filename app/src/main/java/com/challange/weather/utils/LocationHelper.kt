package com.challange.weather.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import android.app.Activity
import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.challange.weather.presentation.home.WeatherSearchActivity
import com.google.android.gms.location.*
import java.lang.ClassCastException

class LocationHelper(
    private val context: Context,
    private val onLocationCompleteListener: OnLocationCompleteListener
    ) : LocationListener {
    /**
     * Stores parameters for requests to the FusedLocationProviderClientApi.
     */
    private var mLocationRequest: LocationRequest? = null
    private val mLocationCallback: LocationCallback
    private val fusedLocationProviderClient: FusedLocationProviderClient =
        FusedLocationProviderClient(context)

    init {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.equals(null)) {
                    return
                }
                for (location in locationResult.locations) {
                    onLocationCompleteListener.getLocationUpdate(location)
                }
            }
        }
        checkLocationSettings()
        createLocationRequest()
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION`. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     *
     *
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     *
     *
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * Requests location updates from the FusedLocationProviderClient.
     */
    fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest!!,
                mLocationCallback,
                Looper.getMainLooper()
            )
        }
    }

    /**
     * Removes location updates from the FusedLocationProviderClient.
     */
    fun stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
    }

    override fun onLocationChanged(location: Location) {
        onLocationCompleteListener.getLocationUpdate(location)
    }

    private fun checkLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(LocationRequest())
        val result = LocationServices.getSettingsClient(
            context
        ).checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(
                    ApiException::class.java
                )
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                             // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            onLocationCompleteListener.onError(resolvable, null)
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        when (requestCode) {
            REQUEST_CODE_RESOLVABLE_API -> when (resultCode) {
                Activity.RESULT_OK -> Toast.makeText(
                    context,
                    "User agreed to make required location settings changes.", Toast.LENGTH_LONG
                ).show()
                Activity.RESULT_CANCELED -> {
                    //Call used to some operations in activity/fragment if the settings is cancelled
                    onLocationCompleteListener.onResolvableApiResponseFailure()
                    Toast.makeText(
                        context,
                        "User choose not to make required location settings changes.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    interface OnLocationCompleteListener {
        fun getLocationUpdate(location: Location?)
        fun onError(resolvableApiException: ResolvableApiException?, error: String?)
        fun onResolvableApiResponseFailure()
    }

    companion object {
        /**
         * Constant used in the location settings dialog.
         */
        const val REQUEST_CODE_RESOLVABLE_API = 0x6
        private val TAG = LocationHelper::class.java.simpleName

        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000

        /**
         * The fastest rate for active location updates. Exact. Updates will never be more frequent
         * than this value.
         */
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }
}