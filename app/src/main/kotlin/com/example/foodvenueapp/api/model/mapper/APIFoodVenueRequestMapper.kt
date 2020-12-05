package com.example.foodvenueapp.api.model.mapper

import com.example.foodvenueapp.api.model.request.APILocation
import com.google.android.gms.maps.model.LatLng

object APIFoodVenueRequestMapper {

    fun mapToAPILocation(position: LatLng): APILocation =
        with(position) {
            APILocation(latitude, longitude)
        }
}