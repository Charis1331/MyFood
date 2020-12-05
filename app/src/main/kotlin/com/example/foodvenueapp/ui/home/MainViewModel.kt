package com.example.foodvenueapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodvenueapp.data.provider.CustomLocationProvider
import com.example.foodvenueapp.data.provider.LocationProviderResult.Successful
import com.example.foodvenueapp.data.provider.LocationProviderResult.Unsuccessful.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class MainViewModel(
    private val locationProvider: CustomLocationProvider
) : ViewModel() {

    private val _userLocation = MutableLiveData<LatLng>()
    val userLocation: LiveData<LatLng> = _userLocation

    private val _locationRetrievalError = MutableLiveData<Boolean>()
    val locationRetrievalError: LiveData<Boolean> = _locationRetrievalError

    private val _rejectedPermissions = MutableLiveData<Boolean>()
    val rejectedPermissions: LiveData<Boolean> = _rejectedPermissions

    private val _rejectedLocationSettings = MutableLiveData<Boolean>()
    val rejectedLocationSettings: LiveData<Boolean> = _rejectedLocationSettings

    fun getLocation() {
        if (_userLocation.value != null) return

        viewModelScope.launch {
            when (val location = locationProvider.getLocation()) {
                is Successful -> {
                    _userLocation.value = location.coordinates
                    _locationRetrievalError.value = false
                }
                ErrorProvider -> {
                    _locationRetrievalError.value = true
                    _rejectedPermissions.value = false
                    _rejectedLocationSettings.value = false
                }
                PermissionRejection -> {
                    _rejectedPermissions.value = true
                    _locationRetrievalError.value = false
                    _rejectedLocationSettings.value = false
                }
                LocationSettingsRejection -> {
                    _rejectedLocationSettings.value = true
                    _rejectedPermissions.value = false
                    _locationRetrievalError.value = false
                }
            }
        }
    }
}