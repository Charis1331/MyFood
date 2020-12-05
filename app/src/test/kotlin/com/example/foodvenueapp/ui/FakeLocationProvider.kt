package com.example.foodvenueapp.ui

import com.example.foodvenueapp.data.provider.CustomLocationProvider
import com.example.foodvenueapp.data.provider.LocationProviderResult
import com.google.android.gms.maps.model.LatLng

internal val USER_POSITION = LatLng(0.0, 0.0)

class FakeLocationProvider : CustomLocationProvider {

    var returnSuccessful = true
    var returnProviderError = false
        set(value) {
            returnSuccessful = false
            field = value
        }
    var returnPermissionRejection = false
        set(value) {
            returnProviderError = false
            returnSuccessful = false
            field = value
        }
    var returnLocationSettingsRejection = false
        set(value) {
            returnPermissionRejection = false
            returnProviderError = false
            returnSuccessful = false
            field = value
        }

    override suspend fun getLocation(): LocationProviderResult =
        when {
            returnSuccessful -> LocationProviderResult.Successful(USER_POSITION)
            returnProviderError -> LocationProviderResult.Unsuccessful.ErrorProvider
            returnPermissionRejection -> LocationProviderResult.Unsuccessful.PermissionRejection
            returnLocationSettingsRejection -> LocationProviderResult.Unsuccessful.LocationSettingsRejection
            else -> LocationProviderResult.Unsuccessful.LocationSettingsRejection
        }
}