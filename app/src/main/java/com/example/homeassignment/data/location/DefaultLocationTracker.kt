package com.example.homeassignment.data.location

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.example.homeassignment.domain.location.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class DefaultLocationTracker @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
): LocationTracker {

    override suspend fun getCurrentLocation(): Location? {

        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        )  == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!hasFineLocationPermission && !hasCoarseLocationPermission && !isGpsEnabled) {
            return null
        }

        return suspendCancellableCoroutine { calc ->

            locationClient.lastLocation.apply {
                if (isComplete) {
                    if (isSuccessful) {
                        calc.resume(result)
                    } else {
                        calc.resume(null)
                    }

                    return@suspendCancellableCoroutine
                }

                addOnSuccessListener {
                    calc.resume(it)
                }

                addOnFailureListener {
                    calc.resume(null)
                }

                addOnCanceledListener {
                    calc.cancel()
                }

            }

        }

    }
}