package com.example.foodvenueapp.domain.model

import com.google.android.gms.maps.model.LatLng

typealias VenueAddress = FieldThatCanBeEmpty
typealias VenueCategory = FieldThatCanBeEmpty

data class FoodVenue(
    val id: String,
    val name: String,
    val distance: FoodVenueDistance,
    val address: VenueAddress,
    val category: VenueCategory,
    val imageUrl: String?,
    val coordinates: LatLng,
    val isSelected: Boolean = false
)

class FoodVenueDistance(val distanceValue: String, val distanceUnit: DistanceUnit)

sealed class DistanceUnit {
    object KiloMeters: DistanceUnit()
    object Meters: DistanceUnit()
}

sealed class FieldThatCanBeEmpty{
    class Valid(val name: String): FieldThatCanBeEmpty()
    object Empty: FieldThatCanBeEmpty()
}