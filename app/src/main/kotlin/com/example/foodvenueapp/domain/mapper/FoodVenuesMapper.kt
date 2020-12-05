package com.example.foodvenueapp.domain.mapper

import com.example.foodvenueapp.api.model.response.APIVenue
import com.example.foodvenueapp.api.model.response.APIVenueLocation
import com.example.foodvenueapp.domain.model.DistanceUnit
import com.example.foodvenueapp.domain.model.FieldThatCanBeEmpty
import com.example.foodvenueapp.domain.model.FoodVenue
import com.example.foodvenueapp.domain.model.FoodVenueDistance
import com.example.foodvenueapp.util.distanceTo
import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat
import kotlin.math.roundToInt

private const val DISTANCE_IN_KM_FORMAT = ("0.#")

object FoodVenuesMapper {

    private val decimalFormat by lazy {
        DecimalFormat(DISTANCE_IN_KM_FORMAT)
    }

    fun mapToDomainFoodVenue(userPosition: LatLng, apiVenueList: List<APIVenue>): List<FoodVenue> =
        apiVenueList.map {
            FoodVenue(
                it.id,
                it.name,
                computeDistanceFromUser(userPosition, it.location),
                computeFieldThatCanBeEmpty(it.location.address),
                computeFieldThatCanBeEmpty(it.category),
                it.imageUrl,
                getVenueCoordinates(it.location)
            )
        }.sortedWith(FoodVenueComparator)

    private fun computeDistanceFromUser(
        userPosition: LatLng,
        venueLocation: APIVenueLocation
    ): FoodVenueDistance =
        with(venueLocation) {
            val distanceBetween =
                userPosition.distanceTo(latitude, longitude)
            getFoodVenueDistance(distanceBetween)
        }

    private fun getFoodVenueDistance(distance: Float): FoodVenueDistance {
        val (distanceValue, distanceUnit) =
            if (distance.canBeRepresentedAsKiloMeters()) {
                val oneDecimalFormattedDistance = decimalFormat.format(distance / 1000)
                Pair(oneDecimalFormattedDistance, DistanceUnit.KiloMeters)
            } else {
                Pair(distance.roundToInt().toString(), DistanceUnit.Meters)
            }
        return FoodVenueDistance(distanceValue, distanceUnit)
    }

    private fun Float.canBeRepresentedAsKiloMeters() =
        div(1000) > 1

    private fun computeFieldThatCanBeEmpty(address: String?): FieldThatCanBeEmpty =
        if (address.isNullOrEmpty()) {
            FieldThatCanBeEmpty.Empty
        } else {
            FieldThatCanBeEmpty.Valid(address)
        }

    private fun getVenueCoordinates(venueLocation: APIVenueLocation): LatLng =
        with(venueLocation) {
            LatLng(latitude, longitude)
        }
}