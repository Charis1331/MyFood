package com.example.foodvenueapp.data.manager

interface RuntimePermissionsManager {

    suspend fun requestLocationPermissions(): LocationPermissionResult
}