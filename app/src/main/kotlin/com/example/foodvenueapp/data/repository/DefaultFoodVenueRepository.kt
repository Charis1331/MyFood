package com.example.foodvenueapp.data.repository

import com.example.foodvenueapp.data.source.DataSource
import com.example.foodvenueapp.domain.model.FoodVenue
import com.example.foodvenueapp.domain.model.ResultWrapper
import com.example.foodvenueapp.domain.model.PositionOfInterest
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

class DefaultFoodVenueRepository(
    private val remoteDataSource: DataSource
) : FoodVenueRepository {

    override suspend fun getVenues(userPosition: LatLng, mapPosition: PositionOfInterest): Flow<ResultWrapper<List<FoodVenue>>> =
        remoteDataSource.getVenues(userPosition, mapPosition)
}