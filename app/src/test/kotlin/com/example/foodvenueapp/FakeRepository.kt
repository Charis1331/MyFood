package com.example.foodvenueapp

import com.example.foodvenueapp.data.repository.FoodVenueRepository
import com.example.foodvenueapp.data.source.FOOD_VENUE_LIST
import com.example.foodvenueapp.domain.model.FoodVenue
import com.example.foodvenueapp.domain.model.PositionOfInterest
import com.example.foodvenueapp.domain.model.ResultWrapper
import com.example.foodvenueapp.domain.model.UNKNOWN_ERROR_CODE
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository(var venues: List<FoodVenue> = FOOD_VENUE_LIST) : FoodVenueRepository {

    var returnGenericError = false
    var returnNetworkError = false
    set(value) {
        returnGenericError = false
        field = value
    }
    var returnSuccess = true
        set(value) {
            returnNetworkError = false
            returnGenericError = false
            field = value
        }

    val errorCode = UNKNOWN_ERROR_CODE

    override suspend fun getVenues(
        userPosition: LatLng,
        mapPosition: PositionOfInterest
    ): Flow<ResultWrapper<List<FoodVenue>>> {
        val resultWrapper = when {
            returnGenericError -> ResultWrapper.Error(errorCode)
            returnNetworkError -> ResultWrapper.NetworkError
            else -> ResultWrapper.Success(venues)
        }

        return flow { emit(resultWrapper) }
    }
}