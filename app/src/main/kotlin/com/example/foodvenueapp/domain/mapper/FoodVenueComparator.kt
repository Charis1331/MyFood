package com.example.foodvenueapp.domain.mapper

import com.example.foodvenueapp.domain.model.DistanceUnit
import com.example.foodvenueapp.domain.model.FoodVenue
import com.example.foodvenueapp.domain.model.FoodVenueDistance

object FoodVenueComparator : Comparator<FoodVenue> {

    override fun compare(o1: FoodVenue, o2: FoodVenue): Int {
        val firstDistance = getDistanceInMeters(o1.distance)
        val secondDistance = getDistanceInMeters(o2.distance)
        return when {
            firstDistance > secondDistance -> 1
            firstDistance == secondDistance -> 0
            else -> -1
        }
    }

    private fun getDistanceInMeters(distance: FoodVenueDistance): Double {
        val distanceInt = distance.distanceValue.toDouble()
        val distanceIsInKiloMeters = distance.distanceUnit == DistanceUnit.KiloMeters
        return if (distanceIsInKiloMeters) {
            distanceInt * 1000
        } else {
            distanceInt
        }
    }
}