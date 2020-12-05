package com.example.foodvenueapp.data.source

import com.example.foodvenueapp.domain.model.FoodVenue
import com.example.foodvenueapp.domain.model.PositionOfInterest
import com.example.foodvenueapp.domain.model.ResultWrapper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface DataSource {

    suspend fun getVenues(
        userPosition: LatLng,
        location: PositionOfInterest
    ): Flow<ResultWrapper<List<FoodVenue>>>
}