package com.example.foodvenueapp.data.provider

import android.annotation.SuppressLint
import android.location.Location
import androidx.fragment.app.FragmentActivity
import com.example.foodvenueapp.data.manager.LocationPermissionResult.Granted
import com.example.foodvenueapp.data.manager.LocationPermissionResult.Rejected.RejectedLocationSettings
import com.example.foodvenueapp.data.manager.LocationPermissionResult.Rejected.RejectedPermission
import com.example.foodvenueapp.data.provider.LocationProviderResult.Successful
import com.example.foodvenueapp.data.provider.LocationProviderResult.Unsuccessful.*
import com.example.foodvenueapp.data.manager.RuntimePermissionsManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class UserLocationProvider(
    private val activity: FragmentActivity,
    private val permissionsManager: RuntimePermissionsManager,
    private val locationRequest: LocationRequest,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : CustomLocationProvider {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(activity)
    }

    override suspend fun getLocation(): LocationProviderResult = withContext(dispatcher) {
        when (permissionsManager.requestLocationPermissions()) {
            Granted -> {
                getProviderLocation()
            }
            RejectedPermission -> {
                PermissionRejection
            }
            RejectedLocationSettings -> {
                LocationSettingsRejection
            }
        }
    }

    private fun stopLocationUpdates(callback: LocationCallback) {
        fusedLocationClient.removeLocationUpdates(callback)
    }

    private fun getLocationCallback(
        doOnSuccess: (coordinates: LatLng) -> Unit,
        doOnError: () -> Unit
    ): LocationCallback =
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                stopLocationUpdates(this)

                val locations = locationResult?.locations
                if (locations.isNullOrEmpty()) {
                    doOnError()
                } else {
                    doOnSuccess(locations.first().getCoordinates())
                }
            }
        }

    private fun Location.getCoordinates(): LatLng =
        LatLng(latitude, longitude)

    @SuppressLint("MissingPermission")
    suspend fun getProviderLocation(): LocationProviderResult =
        suspendCancellableCoroutine { cont ->
            val doOnSuccess: (coordinates: LatLng) -> Unit = { cont.resume(Successful(it)) }
            val doOnError = { cont.resume(ErrorProvider) }

            val callback = getLocationCallback(doOnSuccess, doOnError)
            val looper = activity.mainLooper

            cont.invokeOnCancellation { stopLocationUpdates(callback) }
            fusedLocationClient.requestLocationUpdates(locationRequest, callback, looper)
        }
}

sealed class LocationProviderResult {
    class Successful(val coordinates: LatLng) : LocationProviderResult()
    sealed class Unsuccessful : LocationProviderResult() {
        object ErrorProvider : Unsuccessful()
        object PermissionRejection : Unsuccessful()
        object LocationSettingsRejection : Unsuccessful()
    }
}