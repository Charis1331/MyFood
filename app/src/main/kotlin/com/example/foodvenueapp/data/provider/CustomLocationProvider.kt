package com.example.foodvenueapp.data.provider

interface CustomLocationProvider {

    suspend fun getLocation(): LocationProviderResult
}