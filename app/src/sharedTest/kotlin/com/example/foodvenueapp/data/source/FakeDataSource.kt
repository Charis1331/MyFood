package com.example.foodvenueapp.data.source

import com.example.foodvenueapp.domain.model.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

val FOOD_VENUE_LIST = mutableListOf(
    FoodVenue(
        "0",
        "name0",
        FoodVenueDistance("1", DistanceUnit.Meters),
        FieldThatCanBeEmpty.Valid("address0"),
        FieldThatCanBeEmpty.Valid("category0"),
        "imageUrl",
        LatLng(0.0, 0.0),
        false
    ),
    FoodVenue(
        "1",
        "name1",
        FoodVenueDistance("1", DistanceUnit.KiloMeters),
        FieldThatCanBeEmpty.Empty,
        FieldThatCanBeEmpty.Empty,
        null,
        LatLng(1.0, 1.0),
        false
    ),
)

open class FakeDataSource(var venues: MutableList<FoodVenue>? = mutableListOf()) : DataSource {

    var shouldReturnError = false
    var shouldReturnNetworkError = false

    override suspend fun getVenues(
        userPosition: LatLng,
        location: PositionOfInterest
    ): Flow<ResultWrapper<List<FoodVenue>>> {
        val result = when {
            shouldReturnSuccess() -> ResultWrapper.Success(venues)
            shouldReturnNetworkError -> ResultWrapper.NetworkError
            else -> ResultWrapper.Error()
        }
       return flow { emit(result) }
    }


    private fun shouldReturnSuccess() =
        venues != null && !shouldReturnError && !shouldReturnNetworkError
}